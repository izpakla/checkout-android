/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.operation;

import java.util.concurrent.Callable;

import com.payoneer.checkout.core.PaymentException;
import com.payoneer.checkout.core.WorkerSubscriber;
import com.payoneer.checkout.core.WorkerTask;
import com.payoneer.checkout.core.Workers;
import com.payoneer.checkout.model.BrowserData;
import com.payoneer.checkout.model.OperationResult;
import com.payoneer.checkout.network.PaymentConnection;
import com.payoneer.checkout.risk.RiskProviders;

import android.content.Context;

/**
 * The OperationService providing asynchronous posting of the Operation and communication with the Payment API.
 * This service makes callbacks in the operation listener to notify of request completions.
 */
public final class OperationService {
    private final PaymentConnection paymentConnection;
    private OperationListener listener;
    private WorkerTask<OperationResult> task;
    static volatile BrowserData browserData;

    /**
     * Create a new OperationService
     */
    public OperationService() {
        paymentConnection = new PaymentConnection();
    }

    /**
     * Set the operation listener which will be informed about the state of a operation.
     *
     * @param listener to be informed about the operation being posted.
     */
    public void setListener(OperationListener listener) {
        this.listener = listener;
    }

    /**
     * Stop and unsubscribe from the task that is currently active in this service.
     */
    public void stop() {
        if (task != null) {
            task.unsubscribe();
            task = null;
        }
    }

    /**
     * Check if this service is currently active posting an operation to the Payment API
     *
     * @return true when active, false otherwise
     */
    public boolean isActive() {
        return task != null && task.isSubscribed();
    }

    /**
     * Post an operation to the Payment API
     *
     * @param operation to be posted to the Payment API
     * @param context in which this operation will be posted
     */
    public void postOperation(final Operation operation, final Context context) {

        if (isActive()) {
            throw new IllegalStateException("Already posting operation, stop first");
        }
        task = WorkerTask.fromCallable(new Callable<OperationResult>() {
            @Override
            public OperationResult call() throws PaymentException {
                return asyncPostOperation(operation, context);
            }
        });
        task.subscribe(new WorkerSubscriber<OperationResult>() {
            @Override
            public void onSuccess(OperationResult result) {
                task = null;

                if (listener != null) {
                    listener.onOperationSuccess(result);
                }
            }

            @Override
            public void onError(Throwable cause) {
                task = null;

                if (listener != null) {
                    listener.onOperationError(cause);
                }
            }
        });
        Workers.getInstance().forNetworkTasks().execute(task);
    }

    private OperationResult asyncPostOperation(final Operation operation, final Context context) throws PaymentException {
        paymentConnection.initialize(context);
        addBrowserData(operation, context);
        addRiskProviderRequests(operation, context);
        return paymentConnection.postOperation(operation);
    }

    private void addRiskProviderRequests(final Operation operation, final Context context) {
        RiskProviders riskProviders = RiskProviders.getInstance();
        if (riskProviders != null) {
            operation.putProviderRequests(riskProviders.getRiskProviderRequests(context));
        }
    }

    private void addBrowserData(final Operation operation, final Context context) {
        if (browserData == null) {
            synchronized (OperationService.class) {
                if (browserData == null) {
                    browserData = BrowserDataBuilder.createFromContext(context);
                }
            }
        }
        operation.setBrowserData(browserData);
    }
}
