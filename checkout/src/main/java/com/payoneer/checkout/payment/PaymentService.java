/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.payment;

import com.payoneer.checkout.model.OperationResult;
import com.payoneer.checkout.network.DeleteAccount;
import com.payoneer.checkout.redirect.RedirectRequest;

import android.content.Context;

/**
 * Interface for network services, a NetworkService is responsible for activating and
 * processing a payment through the supported payment network.
 */
public abstract class PaymentService {

    protected PaymentServiceListener listener;

    /**
     * Stop this NetworkService
     */
    public void stop() {
    }

    /**
     * Set the listener in this NetworkService
     *
     * @param listener the listener to be set
     */
    public void setListener(PaymentServiceListener listener) {
        this.listener = listener;
    }

    /**
     * Process the payment through this NetworkService.
     *
     * @param paymentRequest that should be processed
     * @param context in which this payment will be processed
     */
    public void processPayment(PaymentRequest paymentRequest, Context context) {
    }

    /**
     * Delete the AccountRegistration through this NetworkService.
     *
     * @param account to be deleted from this network
     * @param context in which this account will be deleted
     */
    public void deleteAccount(DeleteAccount account, Context context) {
    }

    /**
     * Notify the network service that the payment has been redirected
     *
     * @param request the original redirect request that triggered this result
     * @param result optional redirect result
     */
    public void onRedirectResult(RedirectRequest request, OperationResult result) {
    }
}
