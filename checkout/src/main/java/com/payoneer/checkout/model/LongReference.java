/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.model;

/**
 * This class is designed to hold advanced reference information about payment.
 */
public class LongReference {
    /** mandatory (max 32) */
    private String essential;
    /** optional (max 32) */
    private String extended;
    /** optional (max 32) */
    private String verbose;

    public String getEssential() {
        return essential;
    }

    public void setEssential(final String essential) {
        this.essential = essential;
    }

    public String getExtended() {
        return extended;
    }

    public void setExtended(final String extended) {
        this.extended = extended;
    }

    public String getVerbose() {
        return verbose;
    }

    public void setVerbose(final String verbose) {
        this.verbose = verbose;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("LongReference [");
        if (essential != null) {
            builder.append("essential=").append(essential).append(", ");
        }
        if (extended != null) {
            builder.append("extended=").append(extended).append(", ");
        }
        if (verbose != null) {
            builder.append("verbose=").append(verbose);
        }
        builder.append("]");
        return builder.toString();
    }
}
