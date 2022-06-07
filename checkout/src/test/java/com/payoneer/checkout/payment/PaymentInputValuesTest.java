/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.payment;

import static io.github.jsonSnapshot.SnapshotMatcher.expect;
import static io.github.jsonSnapshot.SnapshotMatcher.start;
import static io.github.jsonSnapshot.SnapshotMatcher.validateSnapshots;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import com.payoneer.checkout.core.PaymentInputCategory;
import com.payoneer.checkout.core.PaymentInputType;
import com.payoneer.checkout.model.AccountInputData;
import com.payoneer.checkout.model.OperationData;
import com.payoneer.checkout.util.GsonHelper;

@RunWith(RobolectricTestRunner.class)
public class PaymentInputValuesTest {

    @BeforeClass
    public static void beforeClass() {
        start();
    }

    @AfterClass
    public static void afterAll() {
        validateSnapshots();
    }

    @Test(expected = IllegalArgumentException.class)
    public void putValue_invalidCategory_exception() {
        PaymentInputValues values = new PaymentInputValues();
        values.putStringValue(null, PaymentInputType.HOLDER_NAME, "Foo");
    }

    @Test(expected = IllegalArgumentException.class)
    public void putValue_invalidName_exception() {
        PaymentInputValues values = new PaymentInputValues();
        values.putStringValue(PaymentInputCategory.INPUTELEMENT, null, "Foo");
    }

    @Test
    public void copyInto() {
        PaymentInputValues values = new PaymentInputValues();
        values.putStringValue(PaymentInputCategory.INPUTELEMENT, PaymentInputType.HOLDER_NAME, "John Doe");
        values.putStringValue(PaymentInputCategory.INPUTELEMENT, PaymentInputType.ACCOUNT_NUMBER, "accountnumber123");
        values.putStringValue(PaymentInputCategory.INPUTELEMENT, PaymentInputType.BANK_CODE, "bankcode123");
        values.putStringValue(PaymentInputCategory.INPUTELEMENT, PaymentInputType.BANK_NAME, "bankname123");
        values.putStringValue(PaymentInputCategory.INPUTELEMENT, PaymentInputType.BIC, "bic123");
        values.putStringValue(PaymentInputCategory.INPUTELEMENT, PaymentInputType.BRANCH, "branch123");
        values.putStringValue(PaymentInputCategory.INPUTELEMENT, PaymentInputType.CITY, "city123");
        values.putStringValue(PaymentInputCategory.INPUTELEMENT, PaymentInputType.EXPIRY_MONTH, "12");
        values.putStringValue(PaymentInputCategory.INPUTELEMENT, PaymentInputType.EXPIRY_YEAR, "2019");
        values.putStringValue(PaymentInputCategory.INPUTELEMENT, PaymentInputType.IBAN, "iban123");
        values.putStringValue(PaymentInputCategory.INPUTELEMENT, PaymentInputType.LOGIN, "login123");
        values.putStringValue(PaymentInputCategory.INPUTELEMENT, PaymentInputType.PASSWORD, "password123");
        values.putStringValue(PaymentInputCategory.INPUTELEMENT, PaymentInputType.VERIFICATION_CODE, "123");
        values.putStringValue(PaymentInputCategory.INPUTELEMENT, PaymentInputType.CUSTOMER_BIRTHDAY, "3");
        values.putStringValue(PaymentInputCategory.INPUTELEMENT, PaymentInputType.CUSTOMER_BIRTHMONTH, "12");
        values.putStringValue(PaymentInputCategory.INPUTELEMENT, PaymentInputType.CUSTOMER_BIRTHYEAR, "72");
        values.putStringValue(PaymentInputCategory.INPUTELEMENT, PaymentInputType.INSTALLMENT_PLANID, "72");
        values.putBooleanValue(PaymentInputCategory.INPUTELEMENT, PaymentInputType.OPTIN, true);
        values.putBooleanValue(PaymentInputCategory.REGISTRATION, PaymentInputType.ALLOW_RECURRENCE, true);
        values.putBooleanValue(PaymentInputCategory.REGISTRATION, PaymentInputType.AUTO_REGISTRATION, true);

        OperationData operationData = new OperationData();
        operationData.setAccount(new AccountInputData());

        values.copyInto(operationData);
        String json = GsonHelper.getInstance().toJson(operationData);
        expect(json).toMatchSnapshot();
    }
}

