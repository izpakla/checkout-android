/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.payment;

import static io.github.jsonSnapshot.SnapshotMatcher.start;
import static io.github.jsonSnapshot.SnapshotMatcher.validateSnapshots;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.payoneer.checkout.core.PaymentException;
import com.payoneer.checkout.core.PaymentInputCategory;
import com.payoneer.checkout.core.PaymentInputType;
import com.payoneer.checkout.test.util.TestUtils;

public class PaymentRequestTest {

    @BeforeAll
    public static void beforeAll() {
        start();
    }

    @AfterAll
    public static void afterAll() {
        validateSnapshots();
    }

    /*
    @Test
    public void putValue_invalidCategory_exception() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            PaymentRequest request = new PaymentRequest("VISA", "CREDIT_CARD", "CHARGE", TestUtils.createTestLinks());
            request.putStringValue(null, PaymentInputType.HOLDER_NAME, "Foo");
        });
    }

    @Test
    public void putValue_invalidName_exception() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            PaymentRequest request = new PaymentRequest("VISA", "CREDIT_CARD", "CHARGE", TestUtils.createTestLinks());
            request.putStringValue(PaymentInputCategory.INPUTELEMENT, null, "Foo");
        });
    }

    @Test
    public void putValue_invalidRegistrationName_exception() {
        Assertions.assertThrows(PaymentException.class, () -> {
            PaymentRequest request = new PaymentRequest("VISA", "CREDIT_CARD", "CHARGE", TestUtils.createTestLinks());
            request.putBooleanValue(PaymentInputCategory.REGISTRATION, PaymentInputType.HOLDER_NAME, true);
        });
    }

    @Test
    public void putValue_invalidInputElementName_exception() {
        Assertions.assertThrows(PaymentException.class, () -> {
            PaymentRequest request = new PaymentRequest("VISA", "CREDIT_CARD", "CHARGE", TestUtils.createTestLinks());
            request.putBooleanValue(PaymentInputCategory.INPUTELEMENT, PaymentInputType.ALLOW_RECURRENCE, true);
        });
    }

    @Test
    public void putValue_success() throws PaymentException {
        PaymentRequest request = new PaymentRequest("VISA", "CREDIT_CARD", "CHARGE", TestUtils.createTestLinks());
        request.putStringValue(PaymentInputCategory.INPUTELEMENT, PaymentInputType.HOLDER_NAME, "John Doe");
        request.putStringValue(PaymentInputCategory.INPUTELEMENT, PaymentInputType.ACCOUNT_NUMBER, "accountnumber123");
        request.putStringValue(PaymentInputCategory.INPUTELEMENT, PaymentInputType.BANK_CODE, "bankcode123");
        request.putStringValue(PaymentInputCategory.INPUTELEMENT, PaymentInputType.BANK_NAME, "bankname123");
        request.putStringValue(PaymentInputCategory.INPUTELEMENT, PaymentInputType.BIC, "bic123");
        request.putStringValue(PaymentInputCategory.INPUTELEMENT, PaymentInputType.BRANCH, "branch123");
        request.putStringValue(PaymentInputCategory.INPUTELEMENT, PaymentInputType.CITY, "city123");
        request.putStringValue(PaymentInputCategory.INPUTELEMENT, PaymentInputType.EXPIRY_MONTH, "12");
        request.putStringValue(PaymentInputCategory.INPUTELEMENT, PaymentInputType.EXPIRY_YEAR, "2019");
        request.putStringValue(PaymentInputCategory.INPUTELEMENT, PaymentInputType.IBAN, "iban123");
        request.putStringValue(PaymentInputCategory.INPUTELEMENT, PaymentInputType.LOGIN, "login123");
        request.putStringValue(PaymentInputCategory.INPUTELEMENT, PaymentInputType.PASSWORD, "password123");
        request.putStringValue(PaymentInputCategory.INPUTELEMENT, PaymentInputType.VERIFICATION_CODE, "123");
        request.putStringValue(PaymentInputCategory.INPUTELEMENT, PaymentInputType.CUSTOMER_BIRTHDAY, "3");
        request.putStringValue(PaymentInputCategory.INPUTELEMENT, PaymentInputType.CUSTOMER_BIRTHMONTH, "12");
        request.putStringValue(PaymentInputCategory.INPUTELEMENT, PaymentInputType.CUSTOMER_BIRTHYEAR, "72");
        request.putStringValue(PaymentInputCategory.INPUTELEMENT, PaymentInputType.INSTALLMENT_PLANID, "72");
        request.putBooleanValue(PaymentInputCategory.INPUTELEMENT, PaymentInputType.OPTIN, true);
        request.putBooleanValue(PaymentInputCategory.REGISTRATION, PaymentInputType.ALLOW_RECURRENCE, true);
        request.putBooleanValue(PaymentInputCategory.REGISTRATION, PaymentInputType.AUTO_REGISTRATION, true);
    }*/
}

