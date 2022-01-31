/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.model;

import java.math.BigDecimal;

/**
 * Payment amount data.
 */
public class PaymentAmount {
    /** amount */
    private BigDecimal amount;
    /** currency */
    private String currency;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(final BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(final String currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("PaymentAmount [");
        if (amount != null) {
            builder.append("amount=").append(amount).append(", ");
        }
        if (currency != null) {
            builder.append("currency=").append(currency);
        }
        builder.append("]");
        return builder.toString();
    }
}
