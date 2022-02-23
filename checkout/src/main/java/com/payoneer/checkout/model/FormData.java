/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.model;

import java.net.URL;

/**
 * Form data to pre-fill network form. Not all data could be provided- it depends what data we know already and what network should been used.
 */
public class FormData {
    /** account-related data to pre-fill a form */
    private AccountFormData account;
    /** customer-related data to pre-fill a form */
    private CustomerFormData customer;
    /** installments plans data */
    private Installments installments;
    /** An URL to the data privacy consent document */
    private URL dataPrivacyConsentUrl;

    public AccountFormData getAccount() {
        return account;
    }

    public void setAccount(final AccountFormData account) {
        this.account = account;
    }

    public CustomerFormData getCustomer() {
        return customer;
    }

    public void setCustomer(final CustomerFormData customer) {
        this.customer = customer;
    }

    public Installments getInstallments() {
        return installments;
    }

    public void setInstallments(final Installments installments) {
        this.installments = installments;
    }

    public URL getDataPrivacyConsentUrl() {
        return dataPrivacyConsentUrl;
    }

    public void setDataPrivacyConsentUrl(final URL dataPrivacyConsentUrl) {
        this.dataPrivacyConsentUrl = dataPrivacyConsentUrl;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("FormData [");
        if (account != null) {
            builder.append("account=").append(account).append(", ");
        }
        if (customer != null) {
            builder.append("customer=").append(customer).append(", ");
        }
        if (installments != null) {
            builder.append("installments=").append(installments).append(", ");
        }
        if (dataPrivacyConsentUrl != null) {
            builder.append("dataPrivacyConsentUrl=").append(dataPrivacyConsentUrl);
        }
        builder.append("]");
        return builder.toString();
    }
}