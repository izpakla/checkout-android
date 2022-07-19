/*
 *
 *   Copyright (c) 2022 Payoneer Germany GmbH
 *   https://www.payoneer.com
 *
 *   This file is open source and available under the MIT license.
 *   See the LICENSE file for more information.
 *
 */

package com.payoneer.checkout.payment.googlepaybraintree;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

import com.payoneer.checkout.payment.PaymentService;

public class GooglePayBraintreePaymentServiceFactoryTest {

    @Test
    public void supports() {
        GooglePayBraintreePaymentServiceFactory factory = new GooglePayBraintreePaymentServiceFactory();
        assertTrue(factory.supports("GOOGLEPAY", "", Collections.singletonList("BRAINTREE")));

        assertFalse(factory.supports("", "", new ArrayList<>()));

        assertFalse(factory.supports("foo", "foo", null));
        assertFalse(factory.supports("foo", "foo", new ArrayList<>()));

        assertFalse(factory.supports("GOOGLEPAY", "", new ArrayList<>()));
        assertFalse(factory.supports("GOOGLEPAY", "", Arrays.asList("foo", "BRAINTREE")));
        assertFalse(factory.supports("GOOGLEPAY", "", null));
    }

    @Test
    public void createService() {
        GooglePayBraintreePaymentServiceFactory factory = new GooglePayBraintreePaymentServiceFactory();
        PaymentService paymentService = factory.createService();
        assertNotNull(paymentService);
        assertTrue(paymentService instanceof GooglePayBraintreePaymentService);
    }
}