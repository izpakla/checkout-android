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
 * Represents a customer's account (payment information for a payment method) that has been registered (i.e. stored).
 */
public class AccountRegistration {
    /** Simple API, always present */
    private Map<String, URL> links;
    /** Simple API, always present */
    private String code;
    /** Simple API, always present */
    @PaymentMethod.Definition
    private String method;
    /** Simple API, always present */
    private String label;
    /** Simple API, always present */
    @NetworkOperationType.Definition
    private String operationType;
    /** Simple API, always present */
    private AccountMask maskedAccount;
    /** Indicates that this account registration is initially selected */
    private Boolean selected;
    /** code of button-label if this network is selected */
    private String button;
    /** An indicator that a form for this network is an empty one, without any text and input elements */
    private Boolean emptyForm;
    /** Form input elements descriptions */
    private List<InputElement> inputElements;
    /** contract data of first possible route. */
    private Map<String, String> contractData;

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

    public String getMethod() {
        return method;
    }

    public void setMethod(final String method) {
        this.method = method;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
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

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(final Boolean selected) {
        this.selected = selected;
    }

    public String getButton() {
        return button;
    }

    public void setButton(final String button) {
        this.button = button;
    }

    public Boolean getEmptyForm() {
        return emptyForm;
    }

    public void setEmptyForm(final Boolean emptyForm) {
        this.emptyForm = emptyForm;
    }

    public List<InputElement> getInputElements() {
        return inputElements;
    }

    public void setInputElements(final List<InputElement> inputElements) {
        this.inputElements = inputElements;
    }

    public Map<String, String> getContractData() {
        return contractData;
    }

    public void setContractData(final Map<String, String> contractData) {
        this.contractData = contractData;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("AccountRegistration [");
        if (links != null) {
            builder.append("links=").append(links).append(", ");
        }
        if (code != null) {
            builder.append("code=").append(code).append(", ");
        }
        if (method != null) {
            builder.append("method=").append(method).append(", ");
        }
        if (label != null) {
            builder.append("label=").append(label).append(", ");
        }
        if (operationType != null) {
            builder.append("operationType=").append(operationType).append(", ");
        }
        if (maskedAccount != null) {
            builder.append("maskedAccount=").append(maskedAccount).append(", ");
        }
        if (selected != null) {
            builder.append("selected=").append(selected).append(", ");
        }
        if (button != null) {
            builder.append("button=").append(button).append(", ");
        }
        if (emptyForm != null) {
            builder.append("emptyForm=").append(emptyForm).append(", ");
        }
        if (inputElements != null) {
            builder.append("inputElements=").append(inputElements).append(", ");
        }
        if (contractData != null) {
            builder.append("contractData=").append(contractData);
        }
        builder.append("]");
        return builder.toString();
    }
}
