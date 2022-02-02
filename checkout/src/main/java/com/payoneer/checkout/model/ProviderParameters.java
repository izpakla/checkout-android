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
 * Describes a collection of provider specific parameters.
 */
public class ProviderParameters {
    /** optional, provider code. */
    private String providerCode;
    /** collection of parameters. */
    private List<Parameter> parameters;

    public String getProviderCode() {
        return providerCode;
    }

    public void setProviderCode(final String providerCode) {
        this.providerCode = providerCode;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(final List<Parameter> parameters) {
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("ProviderParameters [");
        if (providerCode != null) {
            builder.append("providerCode=").append(providerCode).append(", ");
        }
        if (parameters != null) {
            builder.append("parameters=").append(parameters);
        }
        builder.append("]");
        return builder.toString();
    }
}
