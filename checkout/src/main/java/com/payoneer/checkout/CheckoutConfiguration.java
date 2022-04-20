/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout;

import java.net.URL;

import android.content.pm.ActivityInfo;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

/**
 * The CheckoutConfiguration is the class containing information about the payment session.
 * This class contains the listURL and theming of the screens and dialogs.
 */
public final class CheckoutConfiguration implements Parcelable {

    public static final Creator<CheckoutConfiguration> CREATOR = new Creator<CheckoutConfiguration>() {
        @Override
        public CheckoutConfiguration createFromParcel(Parcel in) {
            return new CheckoutConfiguration(in);
        }

        @Override
        public CheckoutConfiguration[] newArray(final int size) {
            return new CheckoutConfiguration[size];
        }
    };
    /**
     * The self url pointing to the payment session list
     */
    private final URL listURL;
    /**
     * The theming to be applied to the screens and dialogs
     */
    private final CheckoutTheme checkoutTheme;
    /**
     * The orientation of the screens, by default it is in locked mode
     */
    private final int orientation;

    private CheckoutConfiguration(final Builder builder) {
        this.listURL = builder.listURL;
        this.checkoutTheme = builder.checkoutTheme;
        this.orientation = builder.orientation;
    }

    private CheckoutConfiguration(final Parcel in) {
        listURL = (URL) in.readSerializable();
        checkoutTheme = in.readParcelable(CheckoutTheme.class.getClassLoader());
        orientation = in.readInt();
    }

    public static Builder createBuilder(final URL listURL) {
        if (listURL == null) {
            throw new IllegalArgumentException("URL cannot be null");
        }
        return new Builder(listURL);
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeSerializable(listURL);
        dest.writeParcelable(checkoutTheme, flags);
        dest.writeInt(orientation);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public URL getListURL() {
        return listURL;
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
        if (listURL != null) {
            builder.append("listURL=").append(listURL).append(", ");
        }
        if (checkoutTheme != null) {
            builder.append("theme=").append(checkoutTheme).append(", ");
        }
        builder.append("orientation=").append(orientation);
        builder.append("]");
        return builder.toString();
    }

    public static final class Builder {
        private final URL listURL;
        private int orientation;
        private CheckoutTheme checkoutTheme;

        /**
         * Create a new default Builder for creating CheckoutConfiguration instances
         *
         * @param listURL mandatory parameter for creating this builder
         */
        private Builder(@NonNull final URL listURL) {
            this.listURL = listURL;
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
         * @return this builder
         */
        public Builder orientation(final int orientation) {
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

        /**
         * Set the checkout theme in this builder
         *
         * @param checkoutTheme containing the theming for the screens and dialogs
         * @return this builder
         */
        public Builder theme(final CheckoutTheme checkoutTheme) {
            if (checkoutTheme == null) {
                throw new IllegalStateException("CheckoutTheme cannot be null");
            }
            this.checkoutTheme = checkoutTheme;
            return this;
        }

        public CheckoutConfiguration build() {
            return new CheckoutConfiguration(this);
        }
    }
}
