/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.risk;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import com.payoneer.checkout.core.PaymentException;
import com.payoneer.checkout.core.WorkerSubscriber;
import com.payoneer.checkout.core.WorkerTask;
import com.payoneer.checkout.core.Workers;
import com.payoneer.checkout.form.DeleteAccount;
import com.payoneer.checkout.form.Operation;
import com.payoneer.checkout.model.OperationResult;
import com.payoneer.checkout.model.ProviderParameters;

import android.content.Context;

/**
 * The RiskService providing asynchronous initialization of risk providers and collection of risk data from these providers.
 * This service makes callbacks in the risk listener to notify of request completions.
 */
public final class RiskService {
    private WorkerTask<Void> initTask;
    private WorkerTask<List<ProviderParameters>> collectTask;
    private RiskListener listener;

    /**
     * Create a new RiskService
     */
    public RiskService() {
    }

    /**
     * Set the risk listener which will be informed about the state of the requests
     *
     * @param listener to be informed about the risk calls
     */
    public void setListener(RiskListener listener) {
        this.listener = listener;
    }

    /**
     * Stop and unsubscribe from the task that is currently active in this service.
     */
    public void stop() {
        WorkerTask.unsubscribe(initTask);
        initTask = null;

        WorkerTask.unsubscribe(collectTask);
        collectTask = null;
    }

    /**
     * Check if this service is currently active e.g. collecting risk data
     *
     * @return true when active, false otherwise
     */
    public boolean isActive() {
        return WorkerTask.isSubscribed(initTask) || WorkerTask.isSubscribed(collectTask);
    }

    /**
     * Initialize risk providers
     *
     * @param context for setting up initialized
     */
    public void initializeRiskProviders(Context context) {

        if (isActive()) {
            throw new IllegalStateException("RiskService is already active, stop first");
        }
        initTask = WorkerTask.fromCallable(new Callable<Void>() {
            @Override
            public Void call() throws PaymentException {
                return asyncInitializeRiskProviders(context);
            }
        });
        initTask.subscribe(new WorkerSubscriber<Void>() {
            @Override
            public void onSuccess(Void value) {
                initTask = null;
                if (listener != null) {
                    listener.onRiskInitializedSuccess();
                }
            }

            @Override
            public void onError(Throwable cause) {
                initTask = null;
                if (listener != null) {
                    listener.onRiskInitializedError(cause);
                }
            }
        });
        Workers.getInstance().forNetworkTasks().execute(initTask);
    }

    /**
     * Post an operation to the Payment API
     *
     * @param operation to be posted to the Payment API
     */
    public List<ProviderParameters> getRiskProviderData(final Operation operation) {

        if (isActive()) {
            throw new IllegalStateException("RiskService is already active, stop first");
        }
        collectTask = WorkerTask.fromCallable(new Callable<List<ProviderParameters>>() {
            @Override
            public List<ProviderParameters> call() throws PaymentException {
                return asyncGetRiskProviderData();
            }
        });
        collectTask.subscribe(new WorkerSubscriber<OperationResult>() {
            @Override
            public void onSuccess(OperationResult result) {
                collectTask = null;

                if (listener != null) {
                    listener.onGetRiskSuccess(result);
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

    private Void asyncInitializeRiskProviders(final Context context) throws PaymentException {
        return null;
    }

    private List<ProviderParameters> asyncGetRiskProviderData() throws PaymentException {
        return Collections.emptyList();
    }
}
