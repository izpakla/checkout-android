/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.model;

/**
 * This class is designed to hold information about HTTP parameter.
 */
public class Parameter {
    /** Simple API, always present */
    private String name;
    /** Simple API, optional */
    private String value;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Parameter [");
        if (name != null) {
            builder.append("name=").append(name).append(", ");
        }
        if (value != null) {
            builder.append("value=").append(value);
        }
        builder.append("]");
        return builder.toString();
    }
}
