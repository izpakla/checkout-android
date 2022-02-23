/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.model;

import java.net.URL;
import java.util.Map;

/**
 * This class is designed to hold information about operation result.
 */
public class OperationResult {
    /** PCI API, optional */
    private Map<String, URL> links;
    /** PCI API, always present */
    private String resultInfo;
    /** PCI API, optional, always present in response to action (POST, UPDATE) */
    private Interaction interaction;
    /** PCI API, optional */
    private Redirect redirect;
    /** Provider response parameters. */
    private ProviderParameters providerResponse;

    public Map<String, URL> getLinks() {
        return links;
    }

    public void setLinks(final Map<String, URL> links) {
        this.links = links;
    }

    public String getResultInfo() {
        return resultInfo;
    }

    public void setResultInfo(final String resultInfo) {
        this.resultInfo = resultInfo;
    }

    public Interaction getInteraction() {
        return interaction;
    }

    public void setInteraction(final Interaction interaction) {
        this.interaction = interaction;
    }

    public Redirect getRedirect() {
        return redirect;
    }

    public void setRedirect(final Redirect redirect) {
        this.redirect = redirect;
    }

    public ProviderParameters getProviderResponse() {
        return providerResponse;
    }

    public void setProviderResponse(final ProviderParameters providerResponse) {
        this.providerResponse = providerResponse;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("OperationResult [");
        if (links != null) {
            builder.append("links=").append(links).append(", ");
        }
        if (resultInfo != null) {
            builder.append("resultInfo=").append(resultInfo).append(", ");
        }
        if (interaction != null) {
            builder.append("interaction=").append(interaction).append(", ");
        }
        if (redirect != null) {
            builder.append("redirect=").append(redirect).append(", ");
        }
        if (providerResponse != null) {
            builder.append("providerResponse=").append(providerResponse);
        }
        builder.append("]");
        return builder.toString();
    }
}
