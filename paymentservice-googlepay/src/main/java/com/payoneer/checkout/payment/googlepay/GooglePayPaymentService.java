/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.payment.googlepay;

import com.payoneer.checkout.network.DeleteAccount;
import com.payoneer.checkout.network.Operation;
import com.payoneer.checkout.model.OperationResult;
import com.payoneer.checkout.payment.PaymentService;
import com.payoneer.checkout.payment.PaymentServiceListener;
import com.payoneer.checkout.redirect.RedirectRequest;

import android.content.Context;

/**
 * GooglePayPaymentService
 */
public final class GooglePayPaymentService extends PaymentService {

    /**
     * Stop this PaymentService
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
     * @param operation that should be processed
     * @param context in which this payment will be processed
     */
    public void processPayment(Operation operation, Context context) {
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
