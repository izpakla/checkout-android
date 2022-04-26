/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.payment;

import com.payoneer.checkout.CheckoutResult;

import androidx.fragment.app.Fragment;

/**
 * Controller handling the communication between the UI and the payment service.
 */
public interface PaymentServiceListener {

    void showCustomFragment(final Fragment customFragment);

    void onProcessPaymentActive();

    void onDeleteAccountActive();

    void onProcessPaymentResult(final CheckoutResult checkoutResult);

    void onDeleteAccountResult(final CheckoutResult checkoutResult);
}
