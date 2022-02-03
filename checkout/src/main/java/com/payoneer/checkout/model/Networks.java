/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.model;

import java.util.Date;
import java.util.List;

/**
 * This class is designed to hold list of applicable and registered payment network descriptions.
 */
public class Networks {
    /** Simple API, always present */
    private List<ApplicableNetwork> applicable;
    /** Simple API, always present */
    private Date resourcesLastUpdate;

    public List<ApplicableNetwork> getApplicable() {
        return applicable;
    }

    public void setApplicable(final List<ApplicableNetwork> applicable) {
        this.applicable = applicable;
    }

    public Date getResourcesLastUpdate() {
        return resourcesLastUpdate;
    }

    public void setResourcesLastUpdate(final Date resourcesLastUpdate) {
        this.resourcesLastUpdate = resourcesLastUpdate;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Networks [");
        if (applicable != null) {
            builder.append("applicable=").append(applicable).append(", ");
        }
        if (resourcesLastUpdate != null) {
            builder.append("resourcesLastUpdate=").append(resourcesLastUpdate);
        }
        builder.append("]");
        return builder.toString();
    }
}
