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
 * This class is designed to hold information about applicable payment network.
 */
public class ApplicableNetwork {
    /** Simple API, always present */
    private String code;
    /** Simple API, always present */
    private String label;
    /** Simple API, always present */
    @PaymentMethod.Definition
    private String method;
    /** Simple API, always present */
    private String grouping;
    /** Simple API, always present */
    @NetworkOperationType.Definition
    private String operationType;
    /** Simple API, always present */
    @RegistrationType.Definition
    private String registration;
    /** Simple API, always present */
    @RegistrationType.Definition
    private String recurrence;
    /** Simple API, always present */
    private Boolean redirect;
    /** Simple API, always present */
    private Map<String, URL> links;
    /** code of button-label if this network is selected */
    private String button;
    /** flag that network is initially selected */
    private Boolean selected;
    /** form data to pre-fill a form */
    private FormData formData;
    /** An indicator that a form for this network is an empty one, without any text and input elements */
    private Boolean emptyForm;
    /** Form elements descriptions */
    private List<InputElement> inputElements;
    /** contract data of first possible route. */
    private Map<String, String> contractData;

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(final String method) {
        this.method = method;
    }

    public String getGrouping() {
        return grouping;
    }

    public void setGrouping(final String grouping) {
        this.grouping = grouping;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(final String operationType) {
        this.operationType = operationType;
    }

    public String getRegistration() {
        return registration;
    }

    public void setRegistration(final String registration) {
        this.registration = registration;
    }

    public String getRecurrence() {
        return recurrence;
    }

    public void setRecurrence(final String recurrence) {
        this.recurrence = recurrence;
    }

    public Boolean getRedirect() {
        return redirect;
    }

    public void setRedirect(final Boolean redirect) {
        this.redirect = redirect;
    }

    public Map<String, URL> getLinks() {
        return links;
    }

    public void setLinks(final Map<String, URL> links) {
        this.links = links;
    }

    public String getButton() {
        return button;
    }

    public void setButton(final String button) {
        this.button = button;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(final Boolean selected) {
        this.selected = selected;
    }

    public FormData getFormData() {
        return formData;
    }

    public void setFormData(final FormData formData) {
        this.formData = formData;
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
        builder.append("ApplicableNetwork [");
        if (code != null) {
            builder.append("code=").append(code).append(", ");
        }
        if (label != null) {
            builder.append("label=").append(label).append(", ");
        }
        if (method != null) {
            builder.append("method=").append(method).append(", ");
        }
        if (grouping != null) {
            builder.append("grouping=").append(grouping).append(", ");
        }
        if (operationType != null) {
            builder.append("operationType=").append(operationType).append(", ");
        }
        if (registration != null) {
            builder.append("registration=").append(registration).append(", ");
        }
        if (recurrence != null) {
            builder.append("recurrence=").append(recurrence).append(", ");
        }
        if (redirect != null) {
            builder.append("redirect=").append(redirect).append(", ");
        }
        if (links != null) {
            builder.append("links=").append(links).append(", ");
        }
        if (button != null) {
            builder.append("button=").append(button).append(", ");
        }
        if (selected != null) {
            builder.append("selected=").append(selected).append(", ");
        }
        if (formData != null) {
            builder.append("formData=").append(formData).append(", ");
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
