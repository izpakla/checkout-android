/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.payment;

import static com.payoneer.checkout.core.PaymentInputCategory.EXTRAELEMENT;
import static com.payoneer.checkout.core.PaymentInputCategory.INPUTELEMENT;
import static com.payoneer.checkout.core.PaymentInputCategory.REGISTRATION;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import com.payoneer.checkout.core.PaymentInputType;
import com.payoneer.checkout.model.AccountInputData;
import com.payoneer.checkout.model.OperationData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Class for storing input values provided by the user, this class can contain
 * both String and Boolean values.
 */
public final class PaymentInputValues implements Parcelable {

    private final List<BooleanInputValue> booleanValues;
    private final List<StringInputValue> stringValues;

    public PaymentInputValues() {
        booleanValues = new ArrayList<>();
        stringValues = new ArrayList<>();
    }

    protected PaymentInputValues(Parcel in) {
        stringValues = in.readArrayList(StringInputValue.class.getClassLoader());
        booleanValues = in.readArrayList(BooleanInputValue.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(stringValues);
        dest.writeList(booleanValues);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PaymentInputValues> CREATOR = new Creator<PaymentInputValues>() {
        @Override
        public PaymentInputValues createFromParcel(Parcel in) {
            return new PaymentInputValues(in);
        }

        @Override
        public PaymentInputValues[] newArray(int size) {
            return new PaymentInputValues[size];
        }
    };

    /**
     * Put a boolean value into this Operation form.
     * Depending on the category and name of the value it will be added to the correct place in the Operation Json Object.
     *
     * @param category category the input value belongs to
     * @param name     name identifying the value
     * @param value    containing the value of the input
     */
    public void putBooleanValue(final String category, final String name, final Boolean value) {
        if (TextUtils.isEmpty(category)) {
            throw new IllegalArgumentException("Category cannot be null or empty");
        }
        if (TextUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        booleanValues.add(new BooleanInputValue(category, name, value));
    }

    /**
     * Put a String value into this Operation form.
     * Depending on the category and name of the value it will be added to the correct place in the Operation Json Object.
     *
     * @param category category the input value belongs to
     * @param name     name identifying the value
     * @param value    containing the value of the input
     */
    public void putStringValue(final String category, final String name, final String value) {
        if (TextUtils.isEmpty(category)) {
            throw new IllegalArgumentException("Category cannot be null or empty");
        }
        if (TextUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        stringValues.add(new StringInputValue(category, name, value));
    }

    /**
     * Copy the String and Boolean values into the OperationData
     *
     * @param operationData into which the values should be copied into
     */
    public void copyInto(final OperationData operationData) {
        Objects.requireNonNull(operationData, "operationData cannot be null");

        for (StringInputValue stringValue : stringValues) {
            copyStringValueInto(operationData, stringValue);
        }
        for (BooleanInputValue booleanValue : booleanValues) {
            copyBooleanValueInto(operationData, booleanValue);
        }
    }

    private void copyBooleanValueInto(final OperationData operationData, final BooleanInputValue inputValue) {
        switch (inputValue.category) {
            case INPUTELEMENT:
                copyInputElementBooleanValueInto(operationData, inputValue.name, inputValue.value);
                break;
            case REGISTRATION:
                copyRegistrationBooleanValueInto(operationData, inputValue);
                break;
            case EXTRAELEMENT:
                copyExtraElementsBooleanValueInto(operationData, inputValue);
                break;
            default:
                String msg = "Operation.putBooleanValue failed for category: " + inputValue.category;
                Log.w("checkout-sdk", msg);
        }
    }

    private void copyStringValueInto(final OperationData operationData, final StringInputValue inputValue) {
        if (INPUTELEMENT.equals(inputValue.category)) {
            copyInputElementStringValueInto(operationData, inputValue);
        } else {
            String msg = "Operation.putStringValue failed for category: " + inputValue.category;
            Log.w("checkout-sdk", msg);
        }
    }

    private void copyInputElementBooleanValueInto(final OperationData operationData, final String name, final boolean value) {
        AccountInputData account = operationData.getAccount();
        if (PaymentInputType.OPTIN.equals(name)) {
            account.setOptIn(value);
        } else {
            String msg = "Operation.Account.putBooleanValue failed for name: " + name;
            Log.w("checkout-sdk", msg);
        }
    }

    private void copyRegistrationBooleanValueInto(final OperationData operationData, final BooleanInputValue inputValue) {
        switch (inputValue.name) {
            case PaymentInputType.ALLOW_RECURRENCE:
                operationData.setAllowRecurrence(inputValue.value);
                break;
            case PaymentInputType.AUTO_REGISTRATION:
                operationData.setAutoRegistration(inputValue.value);
                break;
            default:
                String msg = "Operation.Registration.setBooleanValue failed for name: " + inputValue.name;
                Log.w("checkout-sdk", msg);
        }
    }

    private void copyExtraElementsBooleanValueInto(OperationData operationData, BooleanInputValue inputValue) {
        Map<String, Boolean> extraElementCheckboxes = operationData.getCheckboxes();
        if (extraElementCheckboxes == null) {
            extraElementCheckboxes = new HashMap<>();
        }
        extraElementCheckboxes.put(inputValue.name, inputValue.value);
        operationData.setCheckboxes(extraElementCheckboxes);
    }

    private void copyInputElementStringValueInto(final OperationData operationData, final StringInputValue inputValue) {
        AccountInputData account = operationData.getAccount();
        String name = inputValue.name;
        String value = inputValue.value;

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
                Log.w("checkout-sdk", msg);

        }
    }

    private static class BooleanInputValue implements Parcelable {
        private final String category;
        private final String name;
        private final Boolean value;

        private BooleanInputValue(final String category, final String name, Boolean value) {
            this.category = category;
            this.name = name;
            this.value = value;
        }

        protected BooleanInputValue(Parcel in) {
            category = in.readString();
            name = in.readString();
            byte tmpValue = in.readByte();
            value = tmpValue == 0 ? null : tmpValue == 1;
        }

        public static final Creator<BooleanInputValue> CREATOR = new Creator<BooleanInputValue>() {
            @Override
            public BooleanInputValue createFromParcel(Parcel in) {
                return new BooleanInputValue(in);
            }

            @Override
            public BooleanInputValue[] newArray(int size) {
                return new BooleanInputValue[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(final Parcel parcel, final int i) {
            parcel.writeString(category);
            parcel.writeString(name);
            parcel.writeByte((byte) (value == null ? 0 : value ? 1 : 2));
        }
    }

    private static class StringInputValue implements Parcelable {
        private final String category;
        private final String name;
        private final String value;

        private StringInputValue(final String category, final String name, String value) {
            this.category = category;
            this.name = name;
            this.value = value;
        }

        protected StringInputValue(Parcel in) {
            category = in.readString();
            name = in.readString();
            value = in.readString();
        }

        public static final Creator<StringInputValue> CREATOR = new Creator<StringInputValue>() {
            @Override
            public StringInputValue createFromParcel(Parcel in) {
                return new StringInputValue(in);
            }

            @Override
            public StringInputValue[] newArray(int size) {
                return new StringInputValue[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(final Parcel parcel, final int i) {
            parcel.writeString(category);
            parcel.writeString(name);
            parcel.writeString(value);
        }
    }
}
