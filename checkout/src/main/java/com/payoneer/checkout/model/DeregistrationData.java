/*
 * Copyright (c) 2021 Payoneer Germany GmbH
 * https://payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.model;

/**
 * An information about deregistration options.
 */
public class DeregistrationData {
    /** Simple API, optional - deregister one-click registrations. */
    private Boolean deleteRegistration;
    /** Simple API, optional - deregister recurring registrations. */
    private Boolean deleteRecurrence;

    public Boolean getDeleteRegistration() {
        return deleteRegistration;
    }

    public void setDeleteRegistration(final Boolean deleteRegistration) {
        this.deleteRegistration = deleteRegistration;
    }

    public Boolean getDeleteRecurrence() {
        return deleteRecurrence;
    }

    public void setDeleteRecurrence(final Boolean deleteRecurrence) {
        this.deleteRecurrence = deleteRecurrence;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("DeregistrationData [");
        if (deleteRegistration != null) {
            builder.append("deleteRegistration=").append(deleteRegistration).append(", ");
        }
        if (deleteRecurrence != null) {
            builder.append("deleteRecurrence=").append(deleteRecurrence);
        }
        builder.append("]");
        return builder.toString();
    }
}
