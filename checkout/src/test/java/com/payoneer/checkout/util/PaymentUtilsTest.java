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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import com.payoneer.checkout.R;
import com.payoneer.checkout.core.PaymentInputType;
import com.payoneer.checkout.model.Checkbox;
import com.payoneer.checkout.model.CheckboxMode;
import com.payoneer.checkout.model.ExtraElement;
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
    public void isCardPaymentMethod() {
        assertTrue(PaymentUtils.isCardPaymentMethod(CREDIT_CARD));
        assertTrue(PaymentUtils.isCardPaymentMethod(DEBIT_CARD));
        assertFalse(PaymentUtils.isCardPaymentMethod(WALLET));
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

    @Test
    public void withEmptyRequiredMessage_formatFallbackMessage() {
        ExtraElement extraElement = new ExtraElement();
        extraElement.setName("REQUIRED");
        extraElement.setLabel("Lorem ipsum dolor sit er elit lamet");
        Checkbox checkbox = new Checkbox();
        checkbox.setRequiredMessage("");
        checkbox.setMode(CheckboxMode.REQUIRED);
        extraElement.setCheckbox(checkbox);

        assertEquals(PaymentUtils.getCheckboxRequiredMessage(extraElement), "REQUIRED.requiredMessage");
    }

    @Test
    public void withnullRequiredMessage_formatFallbackMessage() {
        ExtraElement extraElement = new ExtraElement();
        extraElement.setName("REQUIRED");
        Checkbox checkbox = new Checkbox();
        checkbox.setRequiredMessage(null);
        checkbox.setMode(CheckboxMode.REQUIRED);
        extraElement.setCheckbox(checkbox);

        assertEquals(PaymentUtils.getCheckboxRequiredMessage(extraElement), "REQUIRED.requiredMessage");
    }

    @Test
    public void withnullCheckbox_returnNullMessage() {
        ExtraElement extraElement = new ExtraElement();
        extraElement.setCheckbox(null);

        assertNull(PaymentUtils.getCheckboxRequiredMessage(extraElement));
    }

    @Test
    public void withvalidRequiredMessage_returnTheMessage() {
        ExtraElement extraElement = new ExtraElement();
        extraElement.setName("REQUIRED");
        Checkbox checkbox = new Checkbox();
        checkbox.setRequiredMessage("REQUIRED checkbox message");
        checkbox.setMode(CheckboxMode.REQUIRED);
        extraElement.setCheckbox(checkbox);

        assertEquals(PaymentUtils.getCheckboxRequiredMessage(extraElement), "REQUIRED checkbox message");
    }
}
