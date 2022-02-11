/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout;

import android.content.pm.ActivityInfo;

/**
 * The CheckoutInfo is the class containing information about the payment session.
 */
public final class CheckoutInfo {

    /** The self url pointing to the payment session list */
    private String listUrl;

    /** The theming to be applied to the screens and dialogs */
    private CheckoutTheme theme;

    /** The orientation of the screens, by default it is in locked mode */
    private int orientation;

    private CheckoutInfo() {
    }

    private CheckoutInfo(Builder builder) {
        this.listUrl = builder.listUrl;
        this.theme = builder.theme;
        this.orientation = builder.orientation;
    }

    public static Builder createBuilder(final String listUrl) {
        return new Builder(listUrl);
    }

    public String getListUrl() {
        return listUrl;
    }

    public CheckoutTheme getTheme() {
        return theme;
    }

    public int getOrientation() {
        return orientation;
    }

    public static class Builder {
        String listUrl;
        int orientation;
        CheckoutTheme theme;

        /**
         * Create a new default Builder for creating CheckoutInfo instances
         *
         * @param listUrl mandatory parameter for creating this builder
         */
        Builder(final String listUrl) {
            this.listUrl = listUrl;
            this.orientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED;
            this.theme = CheckoutTheme.createDefault();
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

        public Builder setTheme(final CheckoutTheme theme) {
            if (theme == null) {
                throw new IllegalStateException("CheckoutTheme cannot be null");
            }
            this.theme = theme;
            return this;
        }

        public CheckoutInfo build() {
            return new CheckoutInfo(this);
        }
    }
}
