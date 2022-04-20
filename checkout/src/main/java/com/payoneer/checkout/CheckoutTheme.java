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
    private final int toolbarTheme;
    private final int noToolbarTheme;

    private CheckoutTheme(final Builder builder) {
        this.toolbarTheme = builder.toolbarTheme;
        this.noToolbarTheme = builder.noToolbarTheme;
    }

    protected CheckoutTheme(final Parcel in) {
        toolbarTheme = in.readInt();
        noToolbarTheme = in.readInt();
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeInt(toolbarTheme);
        dest.writeInt(noToolbarTheme);
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
            setToolbarTheme(R.style.CheckoutTheme_Toolbar).
            setNoToolbarTheme(R.style.CheckoutTheme_NoToolbar).
            build();
    }

    public int getToolbarTheme() {
        return toolbarTheme;
    }

    public int getNoToolbarTheme() {
        return noToolbarTheme;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("CheckoutTheme [");
        builder.append("toolbarTheme=").append(toolbarTheme).append(", ");
        builder.append("noToolbarTheme=").append(noToolbarTheme);
        builder.append("]");
        return builder.toString();
    }

    public static final class Builder {
        int toolbarTheme;
        int noToolbarTheme;

        Builder() {
        }

        public Builder setToolbarTheme(@StyleRes final int toolbarTheme) {
            this.toolbarTheme = toolbarTheme;
            return this;
        }

        public Builder setNoToolbarTheme(@StyleRes final int noToolbarTheme) {
            this.noToolbarTheme = noToolbarTheme;
            return this;
        }

        public CheckoutTheme build() {
            return new CheckoutTheme(this);
        }
    }
}
