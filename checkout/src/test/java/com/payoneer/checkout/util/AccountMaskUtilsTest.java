package com.payoneer.checkout.util;

/*
 * Copyright (c) 2021 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

import static com.payoneer.checkout.model.PaymentMethod.CREDIT_CARD;
import static com.payoneer.checkout.model.PaymentMethod.DEBIT_CARD;
import static com.payoneer.checkout.model.PaymentMethod.ONLINE_BANK_TRANSFER;
import static com.payoneer.checkout.model.PaymentMethod.WALLET;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import com.payoneer.checkout.model.AccountMask;

/**
 * Class for testing the AccountMaskUtils class
 */
@RunWith(RobolectricTestRunner.class)
public class AccountMaskUtilsTest {

    @Test
    public void getAccountMaskLabelForCards() {
        String numberLabel = "41 *** 1111";
        String displayLabel = "displayLabel";

        String formattedVisaLabel = "Visa •••• 1111";
        String formattedAmexLabel = "American Express •••• 1111";

        AccountMask accountMask = new AccountMask();
        accountMask.setNumber(numberLabel);
        accountMask.setDisplayLabel(displayLabel);

        assertEquals(formattedVisaLabel, AccountMaskUtils.getAccountMaskLabel(accountMask, CREDIT_CARD, "Visa"));
        assertEquals(formattedAmexLabel, AccountMaskUtils.getAccountMaskLabel(accountMask, DEBIT_CARD, "American Express"));
        assertEquals(displayLabel, AccountMaskUtils.getAccountMaskLabel(accountMask, WALLET, ""));
    }

    @Test
    public void getAccountMaskLabelForIban() {
        String ibanLabel = "GB33BUKB2020155555 *** 55";
        String displayLabel = "displayLabel";
        String formattedIbanLabel = "GB33 •••• 55";

        AccountMask accountMask = new AccountMask();
        accountMask.setIban(ibanLabel);
        accountMask.setDisplayLabel(displayLabel);

        assertEquals(formattedIbanLabel, AccountMaskUtils.getAccountMaskLabel(accountMask, ONLINE_BANK_TRANSFER, "Sepa"));
    }

    @Test
    public void formatCardAccountMaskLabel() {
        assertEquals("", AccountMaskUtils.formatCardAccountMaskLabel(null, null));
        assertEquals("", AccountMaskUtils.formatCardAccountMaskLabel("123", null));
        assertEquals("", AccountMaskUtils.formatCardAccountMaskLabel(null, "Visa"));
        assertEquals("Visa 1", AccountMaskUtils.formatCardAccountMaskLabel("1", "Visa"));
        assertEquals("Visa •••• ", AccountMaskUtils.formatCardAccountMaskLabel("1*", "Visa"));
        assertEquals("Visa •••• 1", AccountMaskUtils.formatCardAccountMaskLabel("1 * 1", "Visa"));
        assertEquals("Visa •••• 1111", AccountMaskUtils.formatCardAccountMaskLabel("41 *** 1111", "Visa"));
    }

    @Test
    public void formatIbanAccountMaskLabel() {
        assertEquals("", AccountMaskUtils.formatIbanAccountMaskLabel(null));
        assertEquals("", AccountMaskUtils.formatIbanAccountMaskLabel(""));
        assertEquals("* •••• ", AccountMaskUtils.formatIbanAccountMaskLabel("*"));
        assertEquals("A* •••• ", AccountMaskUtils.formatIbanAccountMaskLabel("A*"));
        assertEquals("AA * •••• 1", AccountMaskUtils.formatIbanAccountMaskLabel("AA * 1"));
        assertEquals("AB1", AccountMaskUtils.formatIbanAccountMaskLabel("AB1"));
        assertEquals("AA12345678", AccountMaskUtils.formatIbanAccountMaskLabel("AA12345678"));
        assertEquals("GB33 •••• 55", AccountMaskUtils.formatIbanAccountMaskLabel("GB33BUKB2020155555 *** 55"));
    }

    @Test
    public void given_past_date_return_expired_true() {
        AccountMask accountMask = createAccountMask(12, 2021);

        boolean isExpired = AccountMaskUtils.isExpired(accountMask);

        assertTrue(isExpired);
    }

    @Test
    public void given_no_date_return_false() {
        AccountMask accountMask = createAccountMask(null, null);

        boolean isExpired = AccountMaskUtils.isExpired(accountMask);

        assertFalse(isExpired);
    }

    @Test
    public void given_future_date_return_expired_false() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR) + 10;
        AccountMask accountMask = createAccountMask(1, year);

        boolean isExpired = AccountMaskUtils.isExpired(accountMask);

        assertFalse(isExpired);
    }

    @Test
    public void given_current_date_return_expired_false() {
        Calendar cal = Calendar.getInstance();
        int curMonth = cal.get(Calendar.MONTH) + 1;
        int curYear = cal.get(Calendar.YEAR);
        AccountMask accountMask = createAccountMask(curMonth, curYear);

        boolean isExpired = AccountMaskUtils.isExpired(accountMask);

        assertFalse(isExpired);
    }

    private AccountMask createAccountMask(final Integer month, final Integer year) {
        AccountMask accountMask = new AccountMask();
        accountMask.setExpiryYear(year);
        accountMask.setExpiryMonth(month);
        return accountMask;
    }
}