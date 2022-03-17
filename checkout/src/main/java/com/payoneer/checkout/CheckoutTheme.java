/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.StyleRes;

/**
 * Class to hold the theme settings of the screens in the Android SDK
 */
public final class CheckoutTheme implements Parcelable {
    private final int paymentListTheme;
    private final int chargePaymentTheme;

    private CheckoutTheme(final Builder builder) {
        this.paymentListTheme = builder.paymentListTheme;
        this.chargePaymentTheme = builder.chargePaymentTheme;
    }

    protected CheckoutTheme(final Parcel in) {
        paymentListTheme = in.readInt();
        chargePaymentTheme = in.readInt();
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeInt(paymentListTheme);
        dest.writeInt(chargePaymentTheme);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CheckoutTheme> CREATOR = new Creator<CheckoutTheme>() {
        @Override
        public CheckoutTheme createFromParcel(Parcel in) {
            return new CheckoutTheme(in);
        }

        @Override
        public CheckoutTheme[] newArray(final int size) {
            return new CheckoutTheme[size];
        }
    };

    public static Builder createBuilder() {
        return new Builder();
    }

    public static CheckoutTheme createDefault() {
        return createBuilder().
            setPaymentListTheme(R.style.CheckoutTheme_Toolbar).
            setChargePaymentTheme(R.style.CheckoutTheme_NoToolbar).
            build();
    }

    public int getPaymentListTheme() {
        return paymentListTheme;
    }

    public int getChargePaymentTheme() {
        return chargePaymentTheme;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("CheckoutTheme [");
        builder.append("paymentListTheme=").append(paymentListTheme).append(", ");
        builder.append("chargePaymentTheme=").append(chargePaymentTheme);
        builder.append("]");
        return builder.toString();
    }

    public static final class Builder {
        int paymentListTheme;
        int chargePaymentTheme;

        Builder() {
        }

        public Builder setPaymentListTheme(@StyleRes final int paymentListTheme) {
            this.paymentListTheme = paymentListTheme;
            return this;
        }

        public Builder setChargePaymentTheme(@StyleRes final int chargePaymentTheme) {
            this.chargePaymentTheme = chargePaymentTheme;
            return this;
        }

        public CheckoutTheme build() {
            return new CheckoutTheme(this);
        }
    }
}
