/*
 * Copyright(c) 2012-2018 optile GmbH. All Rights Reserved.
 * https://www.optile.net
 *
 * This software is the property of optile GmbH. Distribution  of  this
 * software without agreement in writing is strictly prohibited.
 *
 * This software may not be copied, used or distributed unless agreement
 * has been received in full.
 */

package net.optile.example.checkout;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.Callable;

import android.content.Context;
import android.util.Log;
import net.optile.payment.core.PaymentException;
import net.optile.payment.model.ListResult;
import net.optile.payment.network.ListConnection;
import net.optile.payment.util.PaymentUtils;

/**
 * CheckoutPresenter responsible for communicating with the Merchant Backend to obtain a new list URL.
 * Once a new list URL has been loaded from the Merchant backend the optile PaymentPage can be opened.
 */
final class CheckoutPresenter {

    private static String TAG = "pay_CheckoutPresenter";

    private CheckoutView view;

    /**
     * Construct a new CheckoutPresenter
     *
     * @param view the view
     */
    CheckoutPresenter(final CheckoutView view) {
        this.view = view;
    }

    /**
     * Notify the presenter that it should be stopped.
     * Check if there are any pending requests and close these
     */
    public void onStop() {
    }

    /**
     * Handle the received checkout result from the optile Payment SDK.
     *
     * @param result the result received from the SDK
     */
    void handleCheckoutResult(CheckoutResult result) {

        if (result.success) {
            view.showPaymentSuccess();
        } else if (result.paymentResult != null) {
            Log.i(TAG, "CheckoutError[" + result.paymentResult + "]");
        } else {
            Log.i(TAG, "CheckoutError[unknown]");
        }
    }

    /** 
     * Load here the list URL from the Merchant backend and once loaded, instruct the CheckoutView to 
     * open the PaymentPage using the PaymentUI class.
     */
    void initiatePayment() {
        String listUrl = ""; // load the list url here
        view.openPaymentPage(listUrl);
    }
}
