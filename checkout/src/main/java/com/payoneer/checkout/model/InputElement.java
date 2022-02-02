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
 * Form input element description.
 */
public class InputElement {
    /** name */
    private String name;
    /** type */
    private String type;
    /** options */
    private List<SelectOption> options;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public List<SelectOption> getOptions() {
        return options;
    }

    public void setOptions(final List<SelectOption> options) {
        this.options = options;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("InputElement [");
        if (name != null) {
            builder.append("name=").append(name).append(", ");
        }
        if (type != null) {
            builder.append("type=").append(type).append(", ");
        }
        if (options != null) {
            builder.append("options=").append(options);
        }
        builder.append("]");
        return builder.toString();
    }
}
