/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.model;

import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Describes a collection of provider specific parameters.
 */
public class ProviderParameters {
    /** optional, provider code. */
    private String providerCode;
    /** optional, provider type. */
    private String providerType;
    /** collection of parameters. */
    private List<Parameter> parameters;
    /** Simple API, always present */
    private Map<String, URL> links;

    public String getProviderCode() {
        return providerCode;
    }

    public void setProviderCode(final String providerCode) {
        this.providerCode = providerCode;
    }

    public String getProviderType() {
        return providerType;
    }

    public void setProviderType(final String providerType) {
        this.providerType = providerType;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(final List<Parameter> parameters) {
        this.parameters = parameters;
    }

    public Map<String, URL> getLinks() {
        return links;
    }

    public void setLinks(final Map<String, URL> links) {
        this.links = links;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("ProviderParameters [");
        if (providerCode != null) {
            builder.append("providerCode=").append(providerCode).append(", ");
        }
        if (providerType != null) {
            builder.append("providerType=").append(providerType).append(", ");
        }
        if (parameters != null) {
            builder.append("parameters=").append(parameters).append(", ");
        }
        if (links != null) {
            builder.append("links=").append(links);
        }
        builder.append("]");
        return builder.toString();
    }
}
