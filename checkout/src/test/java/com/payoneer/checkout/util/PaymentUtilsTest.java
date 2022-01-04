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
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
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
        // Given
        AccountMask accountMask = getAccountMask(2021, 12);
        LocalDate fixedDate = provideFixedTimeForTesting();

        // When checking if the card is expired
        boolean isExpired = PaymentUtils.isExpired(accountMask, fixedDate);

        // Should be true
        assertTrue(isExpired);
    }

    @Test
    public void given_future_date_return_expired_false() {
        // Given
        AccountMask accountMask = getAccountMask(2022, 12);
        LocalDate fixedDate = provideFixedTimeForTesting();

        // When checking if the card is expired
        boolean isExpired = PaymentUtils.isExpired(accountMask, fixedDate);

        // Should be false
        assertFalse(isExpired);
    }

    @Test
    public void given_current_date_return_expired_false() {
        // Given
        AccountMask accountMask = getAccountMask(2022, 1);
        LocalDate fixedDate = provideFixedTimeForTesting();

        // When checking if the card is expired
        boolean isExpired = PaymentUtils.isExpired(accountMask, fixedDate);

        // Should be false
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

    private AccountMask getAccountMask(final int year, final int month) {
        AccountMask accountMask = new AccountMask();
        accountMask.setExpiryYear(year);
        accountMask.setExpiryMonth(month);
        return accountMask;
    }

    private static LocalDate provideFixedTimeForTesting() {
        ZoneId zoneId = ZoneId.systemDefault();

        LocalDate localDate = LocalDate.of(2022, 1, 31);
        LocalTime localTime = LocalTime.MAX;
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDate, localTime, zoneId);

        Instant instant = zonedDateTime.toInstant();
        Clock clock = Clock.fixed(instant, zoneId);

        Instant timeNow = Instant.now(clock);
        return LocalDateTime.ofInstant(timeNow, ZoneOffset.systemDefault()).toLocalDate();
    }
}
