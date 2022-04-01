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
import com.payoneer.checkout.operation.DeleteAccount;
import com.payoneer.checkout.redirect.RedirectRequest;

import android.content.Context;

/**
 * Presenter to be called by the NetworkService to inform about payment updates and to show i.e. a progress view or progress dialog.
 */
public interface PaymentServiceController {

    /**
     * Notify the controller that the payment service is progressing
     */
    void onProgress(final boolean interrupt);

    /**
     * Get the context in which this controller is operating
     *
     * @return current context
     */
    Context getContext();

    /**
     * Called when PaymentService is done processing the request.
     *
     * @param resultCode code describing the state of the ChechkoutResult
     * @param checkoutResult containing the information describing the result
     */
    void onProcessPaymentResult(final int resultCode, final CheckoutResult checkoutResult);

    /**
     * Called when PaymentService is done deleting the account.
     *
     * @param resultCode code describing the state of the CheckoutResult
     * @param checkoutResult containing the information describing the result
     */
    void onDeleteAccountResult(final int resultCode, final CheckoutResult checkoutResult);
}
