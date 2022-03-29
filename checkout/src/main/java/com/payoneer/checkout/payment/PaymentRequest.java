/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.payment;

import static com.payoneer.checkout.core.PaymentInputCategory.INPUTELEMENT;
import static com.payoneer.checkout.core.PaymentInputCategory.REGISTRATION;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.gson.JsonSyntaxException;
import com.payoneer.checkout.core.PaymentException;
import com.payoneer.checkout.core.PaymentInputType;
import com.payoneer.checkout.model.AccountInputData;
import com.payoneer.checkout.model.ApplicableNetwork;
import com.payoneer.checkout.model.OperationData;
import com.payoneer.checkout.model.PresetAccount;
import com.payoneer.checkout.model.ProviderParameters;
import com.payoneer.checkout.ui.widget.WidgetInputCollector;
import com.payoneer.checkout.util.GsonHelper;
import com.payoneer.checkout.util.PaymentUtils;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * Class holding data for the PaymentRequest
 */
public class PaymentRequest implements Parcelable, WidgetInputCollector {

    public final static Creator<PaymentRequest> CREATOR = new Creator<PaymentRequest>() {
        public PaymentRequest createFromParcel(Parcel in) {
            return new PaymentRequest(in);
        }

        public PaymentRequest[] newArray(int size) {
            return new PaymentRequest[size];
        }
    };
    private final String networkCode;
    private final String paymentMethod;
    private final String operationType;
    private final Map<String, URL> links;
    private final OperationData operationData;

    public PaymentRequest(String networkCode, String paymentMethod, String operationType, Map<String, URL> links) {
        this.networkCode = networkCode;
        this.paymentMethod = paymentMethod;
        this.operationType = operationType;
        this.links = links;

        operationData = new OperationData();
        operationData.setAccount(new AccountInputData());
    }

    private PaymentRequest(Parcel in) {
        this.networkCode = in.readString();
        this.paymentMethod = in.readString();
        this.operationType = in.readString();

        this.links = new HashMap<>();
        in.readMap(links, URL.class.getClassLoader());

        try {
            GsonHelper gson = GsonHelper.getInstance();
            operationData = gson.fromJson(in.readString(), OperationData.class);
        } catch (JsonSyntaxException e) {
            // this should never happen since we use the same GsonHelper
            // to produce these Json strings
            throw new RuntimeException(e);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(networkCode);
        out.writeString(paymentMethod);
        out.writeString(operationType);
        out.writeMap(links);

        GsonHelper gson = GsonHelper.getInstance();
        out.writeString(gson.toJson(operationData));
    }

    public static PaymentRequest fromApplicableNetwork(ApplicableNetwork network) {
        Map<String, URL> links = PaymentUtils.emptyMapIfNull(network.getLinks());
        return new PaymentRequest(network.getCode(), network.getMethod(), network.getOperationType(), links);
    }

    public static PaymentRequest fromPresetAccount(PresetAccount account) {
        Map<String, URL> links = PaymentUtils.emptyMapIfNull(account.getLinks());
        return new PaymentRequest(account.getCode(), account.getMethod(), account.getOperationType(), links);
    }

    public OperationData getOperationData() {
        return operationData;
    }

    public String getOperationType() {
        return operationType;
    }

    public URL getLink(final String link) {
        return links.get(link);
    }

    public URL getOperationLink() {
        return getLink("operation");
    }

    public String getNetworkCode() {
        return networkCode;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setAccountInputData(AccountInputData inputData) {
        operationData.setAccount(inputData);
    }

    /**
     * Put a boolean value into this Operation form.
     * Depending on the category and name of the value it will be added to the correct place in the Operation Json Object.
     *
     * @param category category the input value belongs to
     * @param name name identifying the value
     * @param value containing the value of the input
     */
    @Override
    public void putBooleanValue(String category, String name, boolean value) throws PaymentException {

        if (TextUtils.isEmpty(category)) {
            throw new IllegalArgumentException("Category cannot be null or empty");
        }
        if (TextUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        switch (category) {
            case INPUTELEMENT:
                putInputElementBooleanValue(name, value);
                break;
            case REGISTRATION:
                putRegistrationBooleanValue(name, value);
                break;
            default:
                String msg = "Operation.putBooleanValue failed for category: " + category;
                throw new PaymentException(msg);
        }
    }

    public void setProviderRequest(ProviderParameters params) {
        operationData.setProviderRequest(params);
    }

    /**
     * Put ProviderParameters requests into this operation.
     * If a request with the code and type is already stored, it will be replaced with the new request.
     *
     * @param providerRequests list of requests to be put into this operation
     */
    public void putProviderRequests(List<ProviderParameters> providerRequests) {
        List<ProviderParameters> list = operationData.getProviderRequests();
        if (list == null) {
            list = new ArrayList<>();
            operationData.setProviderRequests(list);
        }
        for (ProviderParameters request : providerRequests) {
            int index = getProviderRequestIndex(request);
            if (index == -1) {
                list.add(request);
            } else {
                list.set(index, request);
            }
        }
    }

    /**
     * Put a String value into this Operation form.
     * Depending on the category and name of the value it will be added to the correct place in the Operation Json Object.
     *
     * @param category category the input value belongs to
     * @param name name identifying the value
     * @param value containing the value of the input
     */
    @Override
    public void putStringValue(String category, String name, String value) throws PaymentException {

        if (TextUtils.isEmpty(category)) {
            throw new IllegalArgumentException("Category cannot be null or empty");
        }
        if (TextUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        switch (category) {
            case INPUTELEMENT:
                putInputElementStringValue(name, value);
                break;
            default:
                String msg = "Operation.putStringValue failed for category: " + category;
                throw new PaymentException(msg);
        }
    }

    private void putInputElementBooleanValue(String name, boolean value) throws PaymentException {
        AccountInputData account = operationData.getAccount();
        switch (name) {
            case PaymentInputType.OPTIN:
                account.setOptIn(value);
                break;
            default:
                String msg = "Operation.Account.putBooleanValue failed for name: " + name;
                throw new PaymentException(msg);
        }
    }

    private void putRegistrationBooleanValue(String name, boolean value) throws PaymentException {
        switch (name) {
            case PaymentInputType.ALLOW_RECURRENCE:
                operationData.setAllowRecurrence(value);
                break;
            case PaymentInputType.AUTO_REGISTRATION:
                operationData.setAutoRegistration(value);
                break;
            default:
                String msg = "Operation.Registration.setBooleanValue failed for name: " + name;
                throw new PaymentException(msg);
        }
    }

    private void putInputElementStringValue(String name, String value) throws PaymentException {
        AccountInputData account = operationData.getAccount();

        switch (name) {
            case PaymentInputType.HOLDER_NAME:
                account.setHolderName(value);
                break;
            case PaymentInputType.ACCOUNT_NUMBER:
                account.setNumber(value);
                break;
            case PaymentInputType.BANK_CODE:
                account.setBankCode(value);
                break;
            case PaymentInputType.BANK_NAME:
                account.setBankName(value);
                break;
            case PaymentInputType.BIC:
                account.setBic(value);
                break;
            case PaymentInputType.BRANCH:
                account.setBranch(value);
                break;
            case PaymentInputType.CITY:
                account.setCity(value);
                break;
            case PaymentInputType.EXPIRY_MONTH:
                account.setExpiryMonth(value);
                break;
            case PaymentInputType.EXPIRY_YEAR:
                account.setExpiryYear(value);
                break;
            case PaymentInputType.IBAN:
                account.setIban(value);
                break;
            case PaymentInputType.LOGIN:
                account.setLogin(value);
                break;
            case PaymentInputType.PASSWORD:
                account.setPassword(value);
                break;
            case PaymentInputType.VERIFICATION_CODE:
                account.setVerificationCode(value);
                break;
            case PaymentInputType.CUSTOMER_BIRTHDAY:
                account.setCustomerBirthDay(value);
                break;
            case PaymentInputType.CUSTOMER_BIRTHMONTH:
                account.setCustomerBirthMonth(value);
                break;
            case PaymentInputType.CUSTOMER_BIRTHYEAR:
                account.setCustomerBirthYear(value);
                break;
            case PaymentInputType.INSTALLMENT_PLANID:
                account.setInstallmentPlanId(value);
                break;
            default:
                String msg = "Operation.Account.putStringValue failed for name: " + name;
                throw new PaymentException(msg);
        }
    }

    private int getProviderRequestIndex(ProviderParameters request) {
        List<ProviderParameters> list = operationData.getProviderRequests();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                ProviderParameters parameters = list.get(i);
                if ((Objects.equals(parameters.getProviderCode(), request.getProviderCode())) &&
                    (Objects.equals(parameters.getProviderType(), request.getProviderType()))) {
                    return i;
                }
            }
        }
        return -1;
    }
}
