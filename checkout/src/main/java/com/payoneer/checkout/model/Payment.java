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
 * This class is designed to hold payment information.
 */
public class Payment {
    /** mandatory */
    private String reference;
    /** mandatory */
    private BigDecimal amount;
    /** mandatory */
    private String currency;
    /** optional (max 128) */
    private String invoiceId;
    /** optional */
    private LongReference longReference;

    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }

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

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(final String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public LongReference getLongReference() {
        return longReference;
    }

    public void setLongReference(final LongReference longReference) {
        this.longReference = longReference;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Payment [");
        if (reference != null) {
            builder.append("reference=").append(reference).append(", ");
        }
        if (amount != null) {
            builder.append("amount=").append(amount).append(", ");
        }
        if (currency != null) {
            builder.append("currency=").append(currency).append(", ");
        }
        if (invoiceId != null) {
            builder.append("invoiceId=").append(invoiceId).append(", ");
        }
        if (longReference != null) {
            builder.append("longReference=").append(longReference);
        }
        builder.append("]");
        return builder.toString();
    }
}
