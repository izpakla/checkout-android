/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.payment.googlepaybraintree;

import java.util.List;

import com.payoneer.checkout.core.PaymentNetworkCodes;
import com.payoneer.checkout.payment.PaymentService;
import com.payoneer.checkout.payment.PaymentServiceFactory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class GooglePayBraintreePaymentServiceFactory implements PaymentServiceFactory {

    private static final String BRAINTREE_PROVIDER_CODE = "BRAINTREE";

    @Override
    public boolean supports(@NonNull String code, @NonNull String method, @Nullable List<String> providers) {
        boolean isGooglePay = PaymentNetworkCodes.GOOGLEPAY.equals(code);
        boolean isBraintree = providers != null && !providers.isEmpty() && providers.get(0).equals(BRAINTREE_PROVIDER_CODE);
        return isGooglePay && isBraintree;
    }

    @Override
    public PaymentService createService() {
        return new GooglePayBraintreePaymentService();
    }
}
