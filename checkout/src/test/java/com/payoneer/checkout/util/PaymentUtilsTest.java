/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.util;

import static com.payoneer.checkout.model.PaymentMethod.CREDIT_CARD;
import static com.payoneer.checkout.model.PaymentMethod.DEBIT_CARD;
import static com.payoneer.checkout.model.PaymentMethod.WALLET;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import com.payoneer.checkout.R;
import com.payoneer.checkout.core.PaymentInputType;
import com.payoneer.checkout.model.AccountMask;
import com.payoneer.checkout.model.InputElement;

import android.content.res.Resources;
import androidx.test.core.app.ApplicationProvider;

/**
 * Class for testing the PaymentUtils class
 */
@RunWith(RobolectricTestRunner.class)
public class PaymentUtilsTest {

    @Test
    public void isTrue() {
        assertTrue(PaymentUtils.isTrue(Boolean.TRUE));
        assertFalse(PaymentUtils.isTrue(Boolean.FALSE));
        assertFalse(PaymentUtils.isTrue(null));
    }

    @Test
    public void trimToEmpty() {
        String empty = "";
        assertEquals(PaymentUtils.trimToEmpty(null), empty);
        assertEquals(PaymentUtils.trimToEmpty("   "), empty);
    }

    @Test
    public void emptyListIfNull() {
        List<String> list = new ArrayList<>();
        assertEquals(list, PaymentUtils.emptyListIfNull(list));
        assertNotNull(PaymentUtils.emptyListIfNull(null));
    }

    @Test
    public void emptyMapIfNull() {
        Map<String, String> map = new HashMap<>();
        assertEquals(map, PaymentUtils.emptyMapIfNull(map));
        assertNotNull(PaymentUtils.emptyMapIfNull(null));
    }

    @Test
    public void toInt() {
        assertEquals(PaymentUtils.toInt(null), 0);
        assertEquals(PaymentUtils.toInt(100), 100);
    }

    @Test
    public void containsExpiryDate() {
        List<InputElement> elements = new ArrayList<>();
        assertFalse(PaymentUtils.containsExpiryDate(elements));

        InputElement month = new InputElement();
        month.setName(PaymentInputType.EXPIRY_MONTH);

        InputElement year = new InputElement();
        year.setName(PaymentInputType.EXPIRY_YEAR);
        elements.add(month);
        elements.add(year);
        assertTrue(PaymentUtils.containsExpiryDate(elements));
    }

    @Test
    public void given_past_date_return_expired_true() {
        AccountMask accountMask = createAccountMask(12, 2021);

        boolean isExpired = PaymentUtils.isExpired(accountMask);

        assertTrue(isExpired);
    }

    @Test
    public void given_no_date_return_false() {
        AccountMask accountMask = createAccountMask(null, null);

        boolean isExpired = PaymentUtils.isExpired(accountMask);

        assertFalse(isExpired);
    }

    @Test
    public void given_future_date_return_expired_false() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR) + 10;
        AccountMask accountMask = createAccountMask(1, year);

        boolean isExpired = PaymentUtils.isExpired(accountMask);

        assertFalse(isExpired);
    }

    @Test
    public void given_current_date_return_expired_false() {
        Calendar cal = Calendar.getInstance();
        int curMonth = cal.get(Calendar.MONTH) + 1;
        int curYear = cal.get(Calendar.YEAR);
        AccountMask accountMask = createAccountMask(curMonth, curYear);

        boolean isExpired = PaymentUtils.isExpired(accountMask);

        assertFalse(isExpired);
    }

    @Test
    public void isCardPaymentMethod() {
        assertTrue(PaymentUtils.isCardPaymentMethod(CREDIT_CARD));
        assertTrue(PaymentUtils.isCardPaymentMethod(DEBIT_CARD));
        assertFalse(PaymentUtils.isCardPaymentMethod(WALLET));
    }

    @Test
    public void getAccountMaskLabel() {
        String numberLabel = "numberLabel";
        String displayLabel = "displayLabel";

        AccountMask accountMask = new AccountMask();
        accountMask.setNumber(numberLabel);
        accountMask.setDisplayLabel(displayLabel);

        assertEquals(numberLabel, PaymentUtils.getAccountMaskLabel(accountMask, CREDIT_CARD));
        assertEquals(numberLabel, PaymentUtils.getAccountMaskLabel(accountMask, DEBIT_CARD));
        assertEquals(displayLabel, PaymentUtils.getAccountMaskLabel(accountMask, WALLET));
    }

    @Test(expected = IOException.class)
    public void readRawResource_missing_resource() throws IOException {
        Resources res = ApplicationProvider.getApplicationContext().getResources();
        PaymentUtils.readRawResource(res, -1);
    }

    @Test
    public void readRawResource_contains_resource() throws IOException {
        Resources res = ApplicationProvider.getApplicationContext().getResources();
        assertNotNull(PaymentUtils.readRawResource(res, R.raw.groups));
    }

    private AccountMask createAccountMask(final Integer month, final Integer year) {
        AccountMask accountMask = new AccountMask();
        accountMask.setExpiryYear(year);
        accountMask.setExpiryMonth(month);
        return accountMask;
    }
}
