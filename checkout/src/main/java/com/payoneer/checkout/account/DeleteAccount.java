/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.account;

import java.net.URL;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class holding DeleteAccount form values
 */
public class DeleteAccount implements Parcelable {

    public final static Creator<DeleteAccount> CREATOR = new Creator<DeleteAccount>() {
        public DeleteAccount createFromParcel(Parcel in) {
            return new DeleteAccount(in);
        }

        public DeleteAccount[] newArray(int size) {
            return new DeleteAccount[size];
        }
    };
    private final URL url;

    public DeleteAccount(final URL url) {
        this.url = url;
    }

    private DeleteAccount(Parcel in) {
        this.url = (URL) in.readSerializable();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeSerializable(url);
    }

    public URL getURL() {
        return url;
    }
}
