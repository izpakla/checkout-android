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
 * This class is designed to hold list of payment networks available for particular transaction based on provided information and result of
 * initialized payment session.
 * <p>
 * An instance of this object is returned as a result of new <code>Transaction</code> initialization, or during list status update via GET
 * method.
 */
public class ListResult {
    /** Simple API, always present */
    private Map<String, URL> links;
    /** Simple API, always present */
    private String resultInfo;
    /** Simple API, optional, always present in response to action (POST, UPDATE) */
    private Interaction interaction;
    /** Simple API, optional */
    private List<AccountRegistration> accounts;
    /** Simple API, optional, always present in native LIST */
    private Networks networks;
    /** Advanced API, optional */
    private ExtraElements extraElements;
    /** Preset account, Simple API, optional, could present only in the LIST-for-PRESET */
    private PresetAccount presetAccount;
    /** LIST type based on operation of next referred actions, could be one of CHARGE, PRESET, PAYOUT, UPDATE. */
    private String operationType;
    /** Indicates whether this LIST is explicitly initialized with permission or denial to delete accounts. */
    private Boolean allowDelete;
    /** The style object passed in the transaction. */
    private Style style;
    /** Payment information, optional */
    private Payment payment;
    /** Collections of the products, optional */
    private List<Product> products;
    /** Integration type used when creating the LIST session, always present */
    private String integrationType;
    /** List of risk providers, optional */
    private List<ProviderParameters> riskProviders;

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

    public List<AccountRegistration> getAccounts() {
        return accounts;
    }

    public void setAccounts(final List<AccountRegistration> accounts) {
        this.accounts = accounts;
    }

    public Networks getNetworks() {
        return networks;
    }

    public void setNetworks(final Networks networks) {
        this.networks = networks;
    }

    public ExtraElements getExtraElements() {
        return extraElements;
    }

    public void setExtraElements(final ExtraElements extraElements) {
        this.extraElements = extraElements;
    }

    public PresetAccount getPresetAccount() {
        return presetAccount;
    }

    public void setPresetAccount(final PresetAccount presetAccount) {
        this.presetAccount = presetAccount;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(final String operationType) {
        this.operationType = operationType;
    }

    public Boolean getAllowDelete() {
        return allowDelete;
    }

    public void setAllowDelete(final Boolean allowDelete) {
        this.allowDelete = allowDelete;
    }

    public Style getStyle() {
        return style;
    }

    public void setStyle(final Style style) {
        this.style = style;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(final Payment payment) {
        this.payment = payment;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(final List<Product> products) {
        this.products = products;
    }

    public String getIntegrationType() {
        return integrationType;
    }

    public void setIntegrationType(final String integrationType) {
        this.integrationType = integrationType;
    }

    public List<ProviderParameters> getRiskProviders() {
        return riskProviders;
    }

    public void setRiskProviders(final List<ProviderParameters> riskProviders) {
        this.riskProviders = riskProviders;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("ListResult [");
        if (links != null) {
            builder.append("links=").append(links).append(", ");
        }
        if (resultInfo != null) {
            builder.append("resultInfo=").append(resultInfo).append(", ");
        }
        if (interaction != null) {
            builder.append("interaction=").append(interaction).append(", ");
        }
        if (accounts != null) {
            builder.append("accounts=").append(accounts).append(", ");
        }
        if (networks != null) {
            builder.append("networks=").append(networks).append(", ");
        }
        if (extraElements != null) {
            builder.append("extraElements=").append(extraElements).append(", ");
        }
        if (presetAccount != null) {
            builder.append("presetAccount=").append(presetAccount).append(", ");
        }
        if (operationType != null) {
            builder.append("operationType=").append(operationType).append(", ");
        }
        if (allowDelete != null) {
            builder.append("allowDelete=").append(allowDelete).append(", ");
        }
        if (style != null) {
            builder.append("style=").append(style).append(", ");
        }
        if (payment != null) {
            builder.append("payment=").append(payment).append(", ");
        }
        if (products != null) {
            builder.append("products=").append(products).append(", ");
        }
        if (integrationType != null) {
            builder.append("integrationType=").append(integrationType);
        }
        builder.append("]");
        return builder.toString();
    }
}
