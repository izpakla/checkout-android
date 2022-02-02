/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.model;

import java.util.List;

/**
 * Installments information.
 */
public class Installments {
    /** payment amount of original payment */
    private PaymentAmount originalPayment;
    /** installments plans */
    private List<InstallmentsPlan> plans;

    public PaymentAmount getOriginalPayment() {
        return originalPayment;
    }

    public void setOriginalPayment(final PaymentAmount originalPayment) {
        this.originalPayment = originalPayment;
    }

    public List<InstallmentsPlan> getPlans() {
        return plans;
    }

    public void setPlans(final List<InstallmentsPlan> plans) {
        this.plans = plans;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Installments [");
        if (originalPayment != null) {
            builder.append("originalPayment=").append(originalPayment).append(", ");
        }
        if (plans != null) {
            builder.append("plans=").append(plans);
        }
        builder.append("]");
        return builder.toString();
    }
}
