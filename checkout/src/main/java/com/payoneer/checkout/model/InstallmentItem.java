/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.model;

import java.math.BigDecimal;
import java.util.Date;

/**
 * An information about particular payment what is involved into installment payment process.
 */
public class InstallmentItem {
    /** The amount of installment (mandatory) */
    private BigDecimal amount;
    /** Installment/payment date */
    private Date date;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(final BigDecimal amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(final Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("InstallmentItem [");
        if (amount != null) {
            builder.append("amount=").append(amount).append(", ");
        }
        if (date != null) {
            builder.append("date=").append(date);
        }
        builder.append("]");
        return builder.toString();
    }
}
