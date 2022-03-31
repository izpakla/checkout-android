/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.page;

import com.payoneer.checkout.CheckoutConfiguration;
import com.payoneer.checkout.payment.PaymentRequest;
import com.payoneer.checkout.ui.model.PaymentSession;

/**
 * The PaymentListView is the interface is the View part of the MVP, this is implemented by the PaymentListActivity
 */
interface PaymentListView extends BasePaymentView {

    /**
     * Clear the payment list
     */
    void clearPaymentList();

    /**
     * Stop loading and show the PaymentSession
     *
     * @param session the payment session to be shown to the user
     */
    void showPaymentSession(PaymentSession session);
}
