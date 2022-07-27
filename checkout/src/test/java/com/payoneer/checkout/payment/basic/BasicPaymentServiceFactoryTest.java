/*
 *
 *   Copyright (c) 2022 Payoneer Germany GmbH
 *   https://www.payoneer.com
 *
 *   This file is open source and available under the MIT license.
 *   See the LICENSE file for more information.
 *
 */

package com.payoneer.checkout.payment.basic;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.payoneer.checkout.core.PaymentNetworkCodes;
import com.payoneer.checkout.model.PaymentMethod;
import com.payoneer.checkout.payment.PaymentService;

public class BasicPaymentServiceFactoryTest {

    @Test
    public void supports() {
        BasicPaymentServiceFactory factory = new BasicPaymentServiceFactory();

        List<String> supportedCodes = Arrays.asList(PaymentNetworkCodes.SEPADD, PaymentNetworkCodes.PAYPAL, PaymentNetworkCodes.WECHATPC_R);
        List<String> supportedMethods = Arrays.asList(PaymentMethod.CREDIT_CARD, PaymentMethod.DEBIT_CARD);

        for (String code : supportedCodes) {
            for (String method : supportedMethods) {
                assertTrue(factory.supports(code, method, null));
            }
        }

        assertFalse(factory.supports(PaymentNetworkCodes.PAYPAL, PaymentMethod.CREDIT_CARD, Collections.singletonList("")));

        assertFalse(factory.supports("", "", null));
        assertFalse(factory.supports("", "", Collections.emptyList()));
    }

    @Test
    public void createService() {
        BasicPaymentServiceFactory factory = new BasicPaymentServiceFactory();
        PaymentService paymentService = factory.createService();
        assertNotNull(paymentService);
        assertTrue(paymentService instanceof BasicPaymentService);
    }
}