/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.model;

/**
 * Represents a custom extra element that the merchant can define to be visualised on a payment page.
 * The element could be a label or a checkbox.
 */
public class ExtraElement {

    /** The name of the extra element. Required */
    private String name;

    /** The label text that should be visualised for this element. Required */
    private String label;

    /** Determines if this extra element is a checkbox, and its additional properties. Optional */
    private Checkbox checkbox;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public Checkbox getCheckbox() {
        return checkbox;
    }

    public void setCheckbox(final Checkbox checkbox) {
        this.checkbox = checkbox;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("ExtraElement [");
        if (name != null) {
            builder.append("name=").append(name).append(", ");
        }
        if (label != null) {
            builder.append("label=").append(label).append(", ");
        }
        if (checkbox != null) {
            builder.append("checkbox=").append(checkbox);
        }
        builder.append("]");
        return builder.toString();
    }
}
