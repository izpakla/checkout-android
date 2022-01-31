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
 * An information about preset account.
 */
public class PresetAccount {
    /** Links (Simple API, always present) */
    private Map<String, URL> links;
    /** Network code (Simple API, always present) */
    private String code;
    /** Simple API, always present */
    @NetworkOperationType.Definition
    private String operationType;
    /** Masked account (Simple API, optional) */
    private AccountMask maskedAccount;
    /** PCI API, optional */
    private Redirect redirect;
    /** Simple API, always present */
    @PaymentMethod.Definition
    private String method;
    /** The following three booleans determine the visibility of the preset warning text */
    private boolean registered;
    private boolean autoRegistration;
    private boolean allowRecurrence;

    public Map<String, URL> getLinks() {
        return links;
    }

    public void setLinks(final Map<String, URL> links) {
        this.links = links;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(final String operationType) {
        this.operationType = operationType;
    }

    public AccountMask getMaskedAccount() {
        return maskedAccount;
    }

    public void setMaskedAccount(final AccountMask maskedAccount) {
        this.maskedAccount = maskedAccount;
    }

    public Redirect getRedirect() {
        return redirect;
    }

    public void setRedirect(final Redirect redirect) {
        this.redirect = redirect;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(final String method) {
        this.method = method;
    }

    public boolean isRegistered() {
        return registered;
    }

    public void setRegistered(final boolean registered) {
        this.registered = registered;
    }

    public boolean isAutoRegistration() {
        return autoRegistration;
    }

    public void setAutoRegistration(final boolean autoRegistration) {
        this.autoRegistration = autoRegistration;
    }

    public boolean isAllowRecurrence() {
        return allowRecurrence;
    }

    public void setAllowRecurrence(final boolean allowRecurrence) {
        this.allowRecurrence = allowRecurrence;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("PresetAccount [");
        if (links != null) {
            builder.append("links=").append(links).append(", ");
        }
        if (code != null) {
            builder.append("code=").append(code).append(", ");
        }
        if (operationType != null) {
            builder.append("operationType=").append(operationType).append(", ");
        }
        if (maskedAccount != null) {
            builder.append("maskedAccount=").append(maskedAccount).append(", ");
        }
        if (redirect != null) {
            builder.append("redirect=").append(redirect).append(", ");
        }
        if (method != null) {
            builder.append("method=").append(method).append(", ");
        }
        builder.append("registered=").append(registered).append(", ");
        builder.append("autoRegistration=").append(autoRegistration).append(", ");
        builder.append("allowRecurrence=").append(allowRecurrence);
        builder.append("]");
        return builder.toString();
    }
}
