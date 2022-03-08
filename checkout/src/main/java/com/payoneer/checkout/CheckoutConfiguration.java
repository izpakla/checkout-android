/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout;

import android.content.pm.ActivityInfo;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

/**
 * The CheckoutConfiguration is the class containing information about the payment session.
 */
public final class CheckoutConfiguration implements Parcelable {

    /**
     * The self url pointing to the payment session list
     */
    private final String listUrl;

    /**
     * The theming to be applied to the screens and dialogs
     */
    private final CheckoutTheme checkoutTheme;

    /**
     * The orientation of the screens, by default it is in locked mode
     */
    private final int orientation;

    private CheckoutConfiguration(Builder builder) {
        this.listUrl = builder.listUrl;
        this.checkoutTheme = builder.checkoutTheme;
        this.orientation = builder.orientation;
    }

    private CheckoutConfiguration(Parcel in) {
        listUrl = in.readString();
        checkoutTheme = in.readParcelable(CheckoutTheme.class.getClassLoader());
        orientation = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(listUrl);
        dest.writeParcelable(checkoutTheme, flags);
        dest.writeInt(orientation);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CheckoutConfiguration> CREATOR = new Creator<CheckoutConfiguration>() {
        @Override
        public CheckoutConfiguration createFromParcel(Parcel in) {
            return new CheckoutConfiguration(in);
        }

        @Override
        public CheckoutConfiguration[] newArray(int size) {
            return new CheckoutConfiguration[size];
        }
    };

    public String getListUrl() {
        return listUrl;
    }

    public CheckoutTheme getCheckoutTheme() {
        return checkoutTheme;
    }

    public int getOrientation() {
        return orientation;
    }

    @NonNull
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("CheckoutConfiguration [");
        if (listUrl != null) {
            builder.append("listUrl=").append(listUrl).append(", ");
        }
        if (checkoutTheme != null) {
            builder.append("theme=").append(checkoutTheme).append(", ");
        }
        builder.append("orientation=").append(orientation);
        builder.append("]");
        return builder.toString();
    }

    static final class Builder {
        String listUrl;
        int orientation;
        CheckoutTheme checkoutTheme;

        /**
         * Create a new default Builder for creating CheckoutConfiguration instances
         *
         * @param listUrl mandatory parameter for creating this builder
         */
        Builder(final String listUrl) {
            this.listUrl = listUrl;
            this.orientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED;
            this.checkoutTheme = CheckoutTheme.createDefault();
        }

        /**
         * Set the orientation of the screens, the following orientation modes are supported:
         * <p>
         * ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
         * ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
         * ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
         * ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
         * ActivityInfo.SCREEN_ORIENTATION_LOCKED
         * <p>
         * The SCREEN_ORIENTATION_LOCKED is by default used.
         *
         * @param orientation mode for the screens
         */
        void setOrientation(final int orientation) {
            switch (orientation) {
                case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
                case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                case ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE:
                case ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT:
                case ActivityInfo.SCREEN_ORIENTATION_LOCKED:
                    this.orientation = orientation;
                    return;
                default:
                    throw new IllegalArgumentException("Orientation mode is not supported: " + orientation);
            }
        }

        void setTheme(final CheckoutTheme checkoutTheme) {
            if (checkoutTheme == null) {
                throw new IllegalStateException("CheckoutTheme cannot be null");
            }
            this.checkoutTheme = checkoutTheme;
        }

        CheckoutConfiguration build() {
            return new CheckoutConfiguration(this);
        }
    }
}
