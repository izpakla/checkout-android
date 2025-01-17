/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.payment;

import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.CopyOnWriteArrayList;

import android.text.TextUtils;
import android.util.Log;

/**
 * Class for looking up a Paymentervice given the code and payment method.
 */
public class PaymentServiceLookup {

    private static final List<PaymentServiceFactory> factories = new CopyOnWriteArrayList<>();

    /**
     * Check if there is a PaymentService that supports the network code and payment method
     *
     * @param code to be checked if it is supported
     * @param method to be checked if it is supported
     * @param providers to be checked if it is supported
     * @return true when supported, false otherwise
     */
    public static boolean supports(String code, String method, List<String> providers) {
        PaymentServiceFactory factory = getFactory(code, method, providers);
        return factory != null;
    }

    /**
     * Lookup a PaymentService for the network code and payment method
     *
     * @param code to be used to lookup a NetworkService
     * @param method to be used to lookup a NetworkService
     * @param providers to be used to lookup a NetworkService
     * @return the NetworkService that can handle the network or null if none found
     */
    public static PaymentService createService(String code, String method, List<String> providers) {
        PaymentServiceFactory factory = getFactory(code, method, providers);
        return factory != null ? factory.createService() : null;
    }

    private static PaymentServiceFactory getFactory(String code, String method, List<String> providers) {
        if (TextUtils.isEmpty(code)) {
            throw new IllegalArgumentException("Code cannot be null or empty");
        }
        if (TextUtils.isEmpty(method)) {
            throw new IllegalArgumentException("Method cannot be null or empty");
        }
        if (factories.size() == 0) {
            initFactories();
        }
        for (PaymentServiceFactory factory : factories) {
            if (factory.supports(code, method, providers)) {
                return factory;
            }
        }
        return null;
    }

    private static void initFactories() {
        synchronized (factories) {
            if (factories.size() > 0) {
                return;
            }
            ServiceLoader<PaymentServiceFactory> loader = ServiceLoader.load(PaymentServiceFactory.class);
            for (PaymentServiceFactory factory : loader) {
                factories.add(factory);
            }
            loadFactory("com.payoneer.checkout.payment.basic.BasicPaymentServiceFactory");
        }
    }

    private static void loadFactory(String className) {
        try {
            PaymentServiceFactory factory = (PaymentServiceFactory) Class.forName(className).newInstance();
            factories.add(factory);
        } catch (Exception e) {
            Log.w("checkout-sdk", e);
        }
    }
}
