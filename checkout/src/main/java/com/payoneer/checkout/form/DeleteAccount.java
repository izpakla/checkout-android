/*
 * Copyright (c) 2021 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.form;

import java.net.URL;

import com.google.gson.JsonSyntaxException;
import com.payoneer.checkout.model.DeregistrationData;
import com.payoneer.checkout.util.GsonHelper;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Class holding DeleteAccount form values
 */
public class DeleteAccount implements Parcelable {

    public final static Parcelable.Creator<DeleteAccount> CREATOR = new Parcelable.Creator<DeleteAccount>() {
        public DeleteAccount createFromParcel(Parcel in) {
            return new DeleteAccount(in);
        }

        public DeleteAccount[] newArray(int size) {
            return new DeleteAccount[size];
        }
    };
    private final URL url;
    private final DeregistrationData deregistrationData;

    public DeleteAccount(URL url) {
        this.url = url;
        deregistrationData = new DeregistrationData();
        deregistrationData.setDeleteRegistration(true);
        deregistrationData.setDeleteRecurrence(true);
    }

    private DeleteAccount(Parcel in) {
        this.url = (URL) in.readSerializable();

        try {
            GsonHelper gson = GsonHelper.getInstance();
            deregistrationData = gson.fromJson(in.readString(), DeregistrationData.class);
        } catch (JsonSyntaxException e) {
            // this should never happen since we use the same GsonHelper
            // to produce these Json strings
            Log.w("Checkout", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeSerializable(url);

        GsonHelper gson = GsonHelper.getInstance();
        out.writeString(gson.toJson(deregistrationData));
    }

    public String toJson() {
        GsonHelper gson = GsonHelper.getInstance();
        return gson.toJson(deregistrationData);
    }

    public URL getURL() {
        return url;
    }
}
