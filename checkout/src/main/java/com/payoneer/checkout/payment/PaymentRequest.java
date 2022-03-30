/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.payment;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.payoneer.checkout.model.AccountInputData;
import com.payoneer.checkout.model.ApplicableNetwork;
import com.payoneer.checkout.model.OperationData;
import com.payoneer.checkout.model.PresetAccount;
import com.payoneer.checkout.operation.Operation;
import com.payoneer.checkout.util.PaymentUtils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class holding data for the PaymentRequest
 */
public class PaymentRequest implements Parcelable {

    private final String networkCode;
    private final String paymentMethod;
    private final String operationType;
    private final Map<String, URL> links;
    private final PaymentInputValues inputValues;

    public final static Creator<PaymentRequest> CREATOR = new Creator<PaymentRequest>() {
        public PaymentRequest createFromParcel(Parcel in) {
            return new PaymentRequest(in);
        }

        public PaymentRequest[] newArray(int size) {
            return new PaymentRequest[size];
        }
    };

    public PaymentRequest(final String networkCode, final String paymentMethod, final String operationType, final Map<String, URL> links,
        final PaymentInputValues inputValues) {
        this.networkCode = networkCode;
        this.paymentMethod = paymentMethod;
        this.operationType = operationType;
        this.links = links;
        this.inputValues = inputValues;
    }

    private PaymentRequest(Parcel in) {
        this.networkCode = in.readString();
        this.paymentMethod = in.readString();
        this.operationType = in.readString();
        this.inputValues = in.readParcelable(PaymentInputValues.class.getClassLoader());
        this.links = new HashMap<>();
        in.readMap(links, URL.class.getClassLoader());
    }

    public static PaymentRequest from(final ApplicableNetwork network, final PaymentInputValues inputValues) {
        Map<String, URL> links = PaymentUtils.emptyMapIfNull(network.getLinks());
        return new PaymentRequest(network.getCode(), network.getMethod(), network.getOperationType(), links, inputValues);
    }

    public static PaymentRequest from(final PresetAccount account, final PaymentInputValues inputValues) {
        Map<String, URL> links = PaymentUtils.emptyMapIfNull(account.getLinks());
        return new PaymentRequest(account.getCode(), account.getMethod(), account.getOperationType(), links, inputValues);
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
        out.writeParcelable(inputValues, 0);
        out.writeMap(links);
    }

    public PaymentInputValues getPaymentInputValues() {
        return inputValues;
    }

    public String getOperationType() {
        return operationType;
    }

    public String getNetworkCode() {
        return networkCode;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public Operation createOperationWithLink(final String link) {
        OperationData operationData = new OperationData();
        operationData.setAccount(new AccountInputData());

        inputValues.copyInto(operationData);
        return new Operation(links.get(link), operationData);
    }
}
