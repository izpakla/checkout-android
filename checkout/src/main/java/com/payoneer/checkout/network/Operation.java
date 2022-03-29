/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.network;

import java.net.URL;

import com.google.gson.JsonSyntaxException;
import com.payoneer.checkout.model.BrowserData;
import com.payoneer.checkout.model.OperationData;
import com.payoneer.checkout.util.GsonHelper;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class holding Operation data
 */
public class Operation implements Parcelable {

    public final static Parcelable.Creator<Operation> CREATOR = new Parcelable.Creator<Operation>() {
        public Operation createFromParcel(Parcel in) {
            return new Operation(in);
        }

        public Operation[] newArray(int size) {
            return new Operation[size];
        }
    };
    private final URL url;
    private final OperationData operationData;

    public Operation(final URL url, final OperationData operationData) {
        this.url = url;
        this.operationData = operationData;
    }

    private Operation(Parcel in) {
        this.url = (URL) in.readSerializable();
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
        out.writeSerializable(url);
        GsonHelper gson = GsonHelper.getInstance();
        out.writeString(gson.toJson(operationData));
    }

    public URL getURL() {
        return url;
    }

    public void setBrowserData(BrowserData browserData) {
        operationData.setBrowserData(browserData);
    }

    public String toJson() {
        GsonHelper gson = GsonHelper.getInstance();
        return gson.toJson(operationData);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Operation [");
        if (url != null) {
            builder.append("url=").append(url).append(", ");
        }
        if (operationData != null) {
            builder.append("operationData=").append(operationData);
        }
        builder.append("]");
        return builder.toString();
    }
}
