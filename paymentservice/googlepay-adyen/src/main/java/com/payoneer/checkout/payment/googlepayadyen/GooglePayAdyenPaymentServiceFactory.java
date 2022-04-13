/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.payment.googlepayadyen;

import com.payoneer.checkout.core.PaymentNetworkCodes;
import com.payoneer.checkout.payment.PaymentService;
import com.payoneer.checkout.payment.PaymentServiceFactory;

public class GooglePayAdyenPaymentServiceFactory implements PaymentServiceFactory {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(String code, String method) {
        return PaymentNetworkCodes.GOOGLEPAY.equals(code);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PaymentService createService() {
        return new GooglePayAdyenPaymentService();
    }
}
