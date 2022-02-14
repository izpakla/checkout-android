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

/**
 * The CheckoutInfo is the class containing information about the payment session.
 */
public final class CheckoutInfo implements Parcelable {

    /** The self url pointing to the payment session list */
    private String listUrl;

    /** The theming to be applied to the screens and dialogs */
    private CheckoutTheme checkoutTheme;

    /** The orientation of the screens, by default it is in locked mode */
    private int orientation;

    private CheckoutInfo() {
    }

    private CheckoutInfo(Builder builder) {
        this.listUrl = builder.listUrl;
        this.checkoutTheme = builder.checkoutTheme;
        this.orientation = builder.orientation;
    }

    protected CheckoutInfo(Parcel in) {
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

    public static final Creator<CheckoutInfo> CREATOR = new Creator<CheckoutInfo>() {
        @Override
        public CheckoutInfo createFromParcel(Parcel in) {
            return new CheckoutInfo(in);
        }

        @Override
        public CheckoutInfo[] newArray(int size) {
            return new CheckoutInfo[size];
        }
    };

    public static Builder createBuilder(final String listUrl) {
        if (listUrl == null) {
            throw new IllegalStateException("CheckoutTheme cannot be null");
        }
        return new Builder(listUrl);
    }

    public String getListUrl() {
        return listUrl;
    }

    public CheckoutTheme getCheckoutTheme() {
        return checkoutTheme;
    }

    public int getOrientation() {
        return orientation;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("CheckoutInfo [");
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

    public static class Builder {
        String listUrl;
        int orientation;
        CheckoutTheme checkoutTheme;

        /**
         * Create a new default Builder for creating CheckoutInfo instances
         *
         * @param listUrl mandatory parameter for creating this builder
         */
        Builder(final String listUrl) {
            this.listUrl = listUrl;
            this.orientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED;
            this.checkoutTheme = CheckoutTheme.createDefault();
        }

        /**
         * Create a new default Builder for creating CheckoutInfo instances
         *
         * @param info mandatory parameter containing CheckoutInfo to use in this builder
         */
        Builder(final CheckoutInfo info) {
            this.listUrl = info.listUrl;
            this.orientation = info.orientation;
            this.checkoutTheme = info.checkoutTheme;
        }

        /**
         * Set the orientation of the Payment Page, the following orientation modes are supported:
         *
         * ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
         * ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
         * ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
         * ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
         * ActivityInfo.SCREEN_ORIENTATION_LOCKED
         *
         * The SCREEN_ORIENTATION_LOCKED is by default used.
         *
         * @param orientation mode for the Payment Page
         */
        public Builder setOrientation(final int orientation) {
            switch (orientation) {
                case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
                case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                case ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE:
                case ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT:
                case ActivityInfo.SCREEN_ORIENTATION_LOCKED:
                    this.orientation = orientation;
                    return this;
                default:
                    throw new IllegalArgumentException("Orientation mode is not supported: " + orientation);
            }
        }

        public Builder setTheme(final CheckoutTheme checkoutTheme) {
            if (checkoutTheme == null) {
                throw new IllegalStateException("CheckoutTheme cannot be null");
            }
            this.checkoutTheme = checkoutTheme;
            return this;
        }

        public CheckoutInfo build() {
            return new CheckoutInfo(this);
        }
    }
}
