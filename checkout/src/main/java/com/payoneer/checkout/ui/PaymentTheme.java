/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui;

import com.payoneer.checkout.R;

/**
 * Class to hold the theme settings of the payment screens in the Android SDK
 */
public final class PaymentTheme {
    private final int paymentListTheme;
    private final int chargePaymentTheme;

    private PaymentTheme(Builder builder) {
        this.paymentListTheme = builder.paymentListTheme;
        this.chargePaymentTheme = builder.chargePaymentTheme;
    }

    public static Builder createBuilder() {
        return new Builder();
    }

    public static PaymentTheme createDefault() {
        return createBuilder().
            setPaymentListTheme(R.style.PaymentTheme_Toolbar).
            setChargePaymentTheme(R.style.PaymentTheme_NoToolbar).
            build();
    }

    public int getPaymentListTheme() {
        return paymentListTheme;
    }

    public int getChargePaymentTheme() {
        return chargePaymentTheme;
    }

    public static final class Builder {
        int paymentListTheme;
        int chargePaymentTheme;

        Builder() {
        }

        public Builder setPaymentListTheme(int paymentListTheme) {
            this.paymentListTheme = paymentListTheme;
            return this;
        }

        public Builder setChargePaymentTheme(int chargePaymentTheme) {
            this.chargePaymentTheme = chargePaymentTheme;
            return this;
        }

        public PaymentTheme build() {
            return new PaymentTheme(this);
        }
    }
}
