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

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class holding payment request data
 */
public class RequestData implements Parcelable {

    private final String listOperationType;
    private final String networkCode;
    private final String paymentMethod;
    private final String operationType;
    private final Map<String, URL> links;
    private final PaymentInputValues inputValues;

    public final static Creator<RequestData> CREATOR = new Creator<RequestData>() {
        public RequestData createFromParcel(Parcel in) {
            return new RequestData(in);
        }

        public RequestData[] newArray(int size) {
            return new RequestData[size];
        }
    };

    public RequestData(final String listOperationType, final String networkCode, final String paymentMethod, final String operationType,
        final Map<String, URL> links, final PaymentInputValues inputValues) {
        this.listOperationType = listOperationType;
        this.networkCode = networkCode;
        this.paymentMethod = paymentMethod;
        this.operationType = operationType;
        this.links = links;
        this.inputValues = inputValues;
    }

    private RequestData(Parcel in) {
        this.listOperationType = in.readString();
        this.networkCode = in.readString();
        this.paymentMethod = in.readString();
        this.operationType = in.readString();
        this.inputValues = in.readParcelable(PaymentInputValues.class.getClassLoader());
        this.links = new HashMap<>();
        in.readMap(links, URL.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(listOperationType);
        out.writeString(networkCode);
        out.writeString(paymentMethod);
        out.writeString(operationType);
        out.writeParcelable(inputValues, 0);
        out.writeMap(links);
    }

    public PaymentInputValues getPaymentInputValues() {
        return inputValues;
    }

    public String getListOperationType() {
        return listOperationType;
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

    public Map<String, URL> getLinks() {
        return links;
    }

    public URL getLink(final String link) {
        return links.get(link);
    }
}
