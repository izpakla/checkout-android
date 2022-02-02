/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.model;

/**
 * This class is designed to hold information checkbox element that is displayed on payment page.
 */
public class Checkbox {
    /** Defines the mode of this Checkbox, required */
    @CheckboxMode.Definition
    private String mode;
    /** Error message that should be displayed if required checkbox is not checked by customer. */
    private String requiredMessage;

    public String getMode() {
        return mode;
    }

    public void setMode(final String mode) {
        this.mode = mode;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Checkbox [");
        if (mode != null) {
            builder.append("mode=").append(mode).append(", ");
        }
        if (requiredMessage != null) {
            builder.append("requiredMessage=").append(requiredMessage);
        }
        builder.append("]");
        return builder.toString();
    }
}
