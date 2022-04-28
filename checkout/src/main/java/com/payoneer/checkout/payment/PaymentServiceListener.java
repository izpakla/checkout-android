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
 * PaymentService listeners should implement this interface in order to receive events from the
 * PaymentService.
 */
public interface PaymentServiceListener {

    void showFragment(final Fragment fragment);

    void onProcessPaymentActive(final boolean finalizing);

    void onProcessPaymentResult(final CheckoutResult checkoutResult);
}
