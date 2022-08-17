/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.payment;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Interface for all payment service factories.
 * A payment service factory is capable of creating a PaymentService instance for a specific PaymentNetwork type.
 */
public interface PaymentServiceFactory {

    /**
     * Check if the network code and payment method are supported by this factory.
     *
     * @param code to be checked if it is supported by this factory
     * @param method to be checked if it is supported by this factory
     * @param providers to be checked if it is supported by this factory
     * @return true when supported, false otherwise
     */
    boolean supports(@NonNull String code, @NonNull String method, @Nullable List<String> providers);

    /**
     * Create a payment service for this specific payment network
     *
     * @return the newly created payment service
     */
    PaymentService createService();
}
