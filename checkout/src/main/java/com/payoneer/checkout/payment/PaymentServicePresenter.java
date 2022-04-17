/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.payment;

import com.payoneer.checkout.CheckoutResult;

import android.content.Context;
import androidx.fragment.app.Fragment;

/**
 * Presenter containing the business logic and handles the communication between the UI and the payment service.
 */
public interface PaymentServicePresenter {

    void setPaymentServiceViewModel(final PaymentServiceViewModel serviceViewModel);

    void showCustomFragment(final Fragment customFragment);

    Context getApplicationContext();

    void onProcessPaymentActive(final RequestData requestData, final boolean interruptible);

    void onDeleteAccountActive(final RequestData requestData);

    void onProcessPaymentResult(final CheckoutResult checkoutResult);

    void onDeleteAccountResult(final CheckoutResult checkoutResult);
}
