/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.model;

import java.util.List;
import java.util.Map;

/**
 * This class is designed to hold information for operation (CHARGE, PAYOUT, UPDATE) with selected payment network.
 */
public class OperationData {
    /** Simple API, optional */
    private AccountInputData account;
    /** Simple API, optional */
    private Boolean autoRegistration;
    /** Simple API, optional */
    private Boolean allowRecurrence;
    /** Advanced API, optional */
    private Map<String, Boolean> checkboxes;
    /** Provider request parameters. */
    private ProviderParameters providerRequest;
    /** List of Provider request parameters. */
    private List<ProviderParameters> providerRequests;
    /** Customer web browser data */
    private BrowserData browserData;

    public AccountInputData getAccount() {
        return account;
    }

    public void setAccount(final AccountInputData account) {
        this.account = account;
    }

    public Boolean getAutoRegistration() {
        return autoRegistration;
    }

    public void setAutoRegistration(final Boolean autoRegistration) {
        this.autoRegistration = autoRegistration;
    }

    public Boolean getAllowRecurrence() {
        return allowRecurrence;
    }

    public void setAllowRecurrence(final Boolean allowRecurrence) {
        this.allowRecurrence = allowRecurrence;
    }

    public Map<String, Boolean> getCheckboxes() {
        return checkboxes;
    }

    public void setCheckboxes(final Map<String, Boolean> checkboxes) {
        this.checkboxes = checkboxes;
    }

    public ProviderParameters getProviderRequest() {
        return providerRequest;
    }

    public void setProviderRequest(final ProviderParameters providerRequest) {
        this.providerRequest = providerRequest;
    }

    public List<ProviderParameters> getProviderRequests() {
        return providerRequests;
    }

    public void setProviderRequests(final List<ProviderParameters> providerRequests) {
        this.providerRequests = providerRequests;
    }

    public BrowserData getBrowserData() {
        return browserData;
    }

    public void setBrowserData(final BrowserData browserData) {
        this.browserData = browserData;
    }


    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("OperationData [");
        if (account != null) {
            builder.append("account=").append(account).append(", ");
        }
        if (autoRegistration != null) {
            builder.append("autoRegistration=").append(autoRegistration).append(", ");
        }
        if (allowRecurrence != null) {
            builder.append("allowRecurrence=").append(allowRecurrence).append(", ");
        }
        if (checkboxes != null) {
            builder.append("checkboxes=").append(checkboxes).append(", ");
        }
        if (providerRequest != null) {
            builder.append("providerRequest=").append(providerRequest).append(", ");
        }
        if (providerRequests != null) {
            builder.append("providerRequests=").append(providerRequests).append(", ");
        }
        if (browserData != null) {
            builder.append("browserData=").append(browserData);
        }
        builder.append("]");
        return builder.toString();
    }
}
