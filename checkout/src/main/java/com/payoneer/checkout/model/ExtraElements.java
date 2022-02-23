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
 * Represents a collection of custom extra elements that the merchant can define to be visualised on payment pages.
 */
public class ExtraElements {

    /**
     * Elements that should be displayed at the top of the payment page.
     * The display order of the elements is defined by the order in which they appear in the collection.
     */
    private List<ExtraElement> top;

    /**
     * Elements that should be displayed at the bottom of the payment page.
     * The display order of the elements is defined by the order in which they appear in the collection.
     */
    private List<ExtraElement> bottom;

    public List<ExtraElement> getTop() {
        return top;
    }

    public void setTop(final List<ExtraElement> top) {
        this.top = top;
    }

    public List<ExtraElement> getBottom() {
        return bottom;
    }

    public void setBottom(final List<ExtraElement> bottom) {
        this.bottom = bottom;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("ExtraElements [");
        if (top != null) {
            builder.append("top=").append(top).append(", ");
        }
        if (bottom != null) {
            builder.append("bottom=").append(bottom);
        }
        builder.append("]");
        return builder.toString();
    }
}