/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.payment;

import com.payoneer.checkout.CheckoutResult;
import com.payoneer.checkout.core.PaymentException;
import com.payoneer.checkout.model.Payment;
import com.payoneer.checkout.operation.DeleteAccount;
import com.payoneer.checkout.redirect.RedirectRequest;

import android.content.Context;
import androidx.fragment.app.Fragment;

/**
 * Presenter to be called by the NetworkService to inform about payment updates and to show i.e. a progress view or progress dialog.
 */
public interface PaymentServicePresenter {

    /**
     * Set the payment service view model in this presenter
     */
    void setPaymentServiceViewModel(final PaymentServiceViewModel serviceViewModel);

    /**
     * Notify the presenter that the payment service is finalizing the payment. This may result
     * in showing a screen that cannot be interrupted.
     */
    void finalizePayment();

    /**
     * Show the payment service fragment
     *
     * @param fragment to be shown
     */
    void showFragment(final Fragment fragment);

    /**
     * Get the application context
     *
     * @return current context
     */
    Context getApplicationContext();

    /**
     * Called when PaymentService is done processing the request.
     *
     * @param checkoutResult containing the information describing the result
     */
    void onProcessPaymentResult(final CheckoutResult checkoutResult);

    /**
     * Called when PaymentService is done deleting the account.
     *
     * @param checkoutResult containing the information describing the result
     */
    void onDeleteAccountResult(final CheckoutResult checkoutResult);
}
