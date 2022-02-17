/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.risk;

/**
 * RiskException containing the details of the risk error that occurred while processing risk providers
 */
public class RiskException extends Exception {

    /**
     * {@inheritDoc}
     */
    public RiskException(final String message) {
        super(message);
    }

    /**
     * {@inheritDoc}
     */
    public RiskException(final Throwable cause) {
        super(cause.getMessage(), cause);
    }

    /**
     * {@inheritDoc}
     */
    public RiskException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
