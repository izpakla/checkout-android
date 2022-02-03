/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.model;

/**
 * Option description.
 */
public class SelectOption {
    /** value */
    private String value;
    /** a flag for the option to be preselected - shown first in the drop-down list */
    private Boolean selected;

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(final Boolean selected) {
        this.selected = selected;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("SelectOption [");
        if (value != null) {
            builder.append("value=").append(value).append(", ");
        }
        if (selected != null) {
            builder.append("selected=").append(selected);
        }
        builder.append("]");
        return builder.toString();
    }
}
