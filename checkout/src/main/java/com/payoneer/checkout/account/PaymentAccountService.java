/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.account;

import java.util.concurrent.Callable;

import com.payoneer.checkout.core.PaymentException;
import com.payoneer.checkout.core.WorkerSubscriber;
import com.payoneer.checkout.core.WorkerTask;
import com.payoneer.checkout.core.Workers;
import com.payoneer.checkout.model.OperationResult;
import com.payoneer.checkout.network.PaymentConnection;

import android.content.Context;

/**
 * The PaymentAccountService providing asynchronous deletion of saved payment accounts.
 * This service makes callbacks in the payment account listener to notify of request completions.
 */
public final class PaymentAccountService {
    private final PaymentConnection paymentConnection;
    private PaymentAccountListener listener;
    private WorkerTask<OperationResult> task;

    /**
     * Create a new PaymentAccountService
     */
    public PaymentAccountService() {
        paymentConnection = new PaymentConnection();
    }

    /**
     * Set the payment account listener which will be informed about the state of a request.
     *
     * @param listener to be informed about the delete request.
     */
    public void setListener(PaymentAccountListener listener) {
        this.listener = listener;
    }

    /**
     * Stop and unsubscribe from the task that is currently active in this service.
     */
    public void stop() {
        WorkerTask.unsubscribe(task);
        task = null;
    }

    /**
     * Check if this service is currently active
     *
     * @return true when active, false otherwise
     */
    public boolean isActive() {
        return WorkerTask.isSubscribed(task);
    }

    /**
     * Delete a saved account
     *
     * @param account to be deleted
     * @param applicationContext in which this account will be deleted
     */
    public void deleteAccount(final DeleteAccount account, final Context applicationContext) {

        if (isActive()) {
            throw new IllegalStateException("PaymentAccountService is already active, stop first");
        }
        task = WorkerTask.fromCallable(new Callable<OperationResult>() {
            @Override
            public OperationResult call() throws PaymentException {
                return asyncDeleteAccount(account, applicationContext);
            }
        });
        task.subscribe(new WorkerSubscriber<OperationResult>() {
            @Override
            public void onSuccess(OperationResult result) {
                task = null;

                if (listener != null) {
                    listener.onDeleteAccountSuccess(result);
                }
            }

            @Override
            public void onError(Throwable cause) {
                task = null;

                if (listener != null) {
                    listener.onDeleteAccountError(cause);
                }
            }
        });
        Workers.getInstance().forNetworkTasks().execute(task);
    }

    private OperationResult asyncDeleteAccount(final DeleteAccount account, final Context context) throws PaymentException {
        paymentConnection.initialize(context);
        return paymentConnection.deleteAccount(account);
    }
}
