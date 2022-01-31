/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.model;

/**
 * This class is designed to hold interaction information providing a recommendation on how to proceed.
 */
public class Interaction {
    /** Simple API, always present */
    @InteractionCode.Definition
    private String code;
    /** Simple API, always present */
    @InteractionReason.Definition
    private String reason;

    /**
     * Construct an empty Interaction Object
     */
    public Interaction() {
    }

    /**
     * Construct a new Interaction Object with the predefined code and reason
     *
     * @param code the code to set.
     * @param reason the reason to set.
     */
    public Interaction(@InteractionCode.Definition String code, @InteractionReason.Definition String reason) {
        this.code = code;
        this.reason = reason;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(final String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Interaction [");
        if (code != null) {
            builder.append("code=").append(code).append(", ");
        }
        if (reason != null) {
            builder.append("reason=").append(reason);
        }
        builder.append("]");
        return builder.toString();
    }
}
