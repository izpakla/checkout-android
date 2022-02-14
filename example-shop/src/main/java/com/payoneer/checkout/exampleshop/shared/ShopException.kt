/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.exampleshop.shared;

/**
 * Generic ShopException
 */
public class ShopException extends Exception {

    /**
     * {@inheritDoc}
     *
     * @param detailMessage the detail message
     * @param cause the cause
     */
    public ShopException(final String detailMessage, final Throwable cause) {
        super(detailMessage, cause);
    }
}
