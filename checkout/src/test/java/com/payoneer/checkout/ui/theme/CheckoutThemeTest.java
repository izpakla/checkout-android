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

        assertEquals(theme.getToolbarTheme(), R.style.CheckoutTheme_Toolbar);
        assertEquals(theme.getNoToolbarTheme(), R.style.CheckoutTheme_NoToolbar);
    }

    @Test
    public void getPaymentListTheme() {
        int value = R.style.CheckoutTheme;
        CheckoutTheme theme = CheckoutTheme.createBuilder().
            setToolbarTheme(value).build();
        assertEquals(theme.getToolbarTheme(), value);
    }

    @Test
    public void getChargePaymentTheme() {
        int value = R.style.CheckoutTheme;
        CheckoutTheme theme = CheckoutTheme.createBuilder().
            setNoToolbarTheme(value).build();
        assertEquals(theme.getNoToolbarTheme(), value);
    }
}
