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

/**
 * This class is designed to hold information to redirect customers browser as a result of operation execution.
 */
public class Redirect {
    /** Simple API, always present */
    private URL url;
    /** Simple API, always present */
    @HttpMethod.Definition
    private String method;
    /** Simple API, optional */
    private List<Parameter> parameters;
    /** Simple API, optional */
    private Boolean suppressIFrame;
    /** Simple API, always present in new transactions */
    private String type;

    public URL getUrl() {
        return url;
    }

    public void setUrl(final URL url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(final String method) {
        this.method = method;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(final List<Parameter> parameters) {
        this.parameters = parameters;
    }

    public Boolean getSuppressIFrame() {
        return suppressIFrame;
    }

    public void setSuppressIFrame(final Boolean suppressIFrame) {
        this.suppressIFrame = suppressIFrame;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Redirect [");
        if (url != null) {
            builder.append("url=").append(url).append(", ");
        }
        if (method != null) {
            builder.append("method=").append(method).append(", ");
        }
        if (parameters != null) {
            builder.append("parameters=").append(parameters).append(", ");
        }
        if (suppressIFrame != null) {
            builder.append("suppressIFrame=").append(suppressIFrame).append(", ");
        }
        if (type != null) {
            builder.append("type=").append(type);
        }
        builder.append("]");
        return builder.toString();
    }
}
