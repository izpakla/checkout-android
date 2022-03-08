/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.theme;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.payoneer.checkout.CheckoutTheme;
import com.payoneer.checkout.R;

public class CheckoutThemeTest {

    @Test
    public void createBuilder() {
        CheckoutTheme.Builder builder = CheckoutTheme.createBuilder();
        assertNotNull(builder);
    }

    @Test
    public void createDefault() {
        CheckoutTheme theme = CheckoutTheme.createDefault();
        assertNotNull(theme);

        assertEquals(theme.getPaymentListTheme(), R.style.PaymentTheme_Toolbar);
        assertEquals(theme.getChargePaymentTheme(), R.style.PaymentTheme_NoToolbar);
    }

    @Test
    public void getPaymentListTheme() {
        int value = R.style.PaymentTheme;
        CheckoutTheme theme = CheckoutTheme.createBuilder().
            setPaymentListTheme(value).build();
        assertEquals(theme.getPaymentListTheme(), value);
    }

    @Test
    public void getChargePaymentTheme() {
        int value = R.style.PaymentTheme;
        CheckoutTheme theme = CheckoutTheme.createBuilder().
            setChargePaymentTheme(value).build();
        assertEquals(theme.getChargePaymentTheme(), value);
    }
}
