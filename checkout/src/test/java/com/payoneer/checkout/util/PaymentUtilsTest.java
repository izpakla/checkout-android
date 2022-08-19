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
import java.net.MalformedURLException;
import java.net.URL;
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
import com.payoneer.checkout.model.InputElement;
import com.payoneer.checkout.model.ListResult;
import com.payoneer.checkout.model.Parameter;

import android.content.res.Resources;
import androidx.test.core.app.ApplicationProvider;
import net.bytebuddy.pool.TypePool;

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
    public void toBoolean() {
        assertTrue(PaymentUtils.toBoolean(null, true));
        assertFalse(PaymentUtils.toBoolean(new Boolean(false), true));
    }

    @Test
    public void toInt() {
        assertEquals(PaymentUtils.toInt(null), 0);
        assertEquals(PaymentUtils.toInt(100), 100);
    }

    @Test
    public void trimToEmpty() {
        String empty = "";
        assertEquals(PaymentUtils.trimToEmpty(null), empty);
        assertEquals(PaymentUtils.trimToEmpty("   "), empty);
    }

    @Test
    public void format() {
        assertEquals("12 / 22", PaymentUtils.format("%1$02d / %2$d", 12, 22));
    }

    @Test
    public void isCardPaymentMethod() {
        assertTrue(PaymentUtils.isCardPaymentMethod(CREDIT_CARD));
        assertTrue(PaymentUtils.isCardPaymentMethod(DEBIT_CARD));
        assertFalse(PaymentUtils.isCardPaymentMethod(WALLET));
    }

    @Test
    public void equalsAsString() {
        Integer obj1 = 12;
        Integer obj2 = 12;
        Integer obj3 = 13;

        assertFalse(PaymentUtils.equalsAsString(null, null));
        assertFalse(PaymentUtils.equalsAsString(obj1, null));
        assertFalse(PaymentUtils.equalsAsString(null, obj2));
        assertFalse(PaymentUtils.equalsAsString(null, obj2));
        assertFalse(PaymentUtils.equalsAsString(obj1, obj3));
        assertTrue(PaymentUtils.equalsAsString(obj1, obj2));
    }

    @Test
    public void containsExpiryDate() {
        List<InputElement> elements = new ArrayList<>();
        assertFalse(PaymentUtils.containsExpiryDate(elements));

        InputElement foo = new InputElement();
        foo.setName("foo");
        elements.add(foo);
        assertFalse(PaymentUtils.containsExpiryDate(elements));

        elements.clear();
        InputElement empty = new InputElement();
        empty.setName("");
        elements.add(empty);
        assertFalse(PaymentUtils.containsExpiryDate(elements));

        elements.clear();
        InputElement month = new InputElement();
        month.setName(PaymentInputType.EXPIRY_MONTH);
        elements.add(month);
        assertFalse(PaymentUtils.containsExpiryDate(elements));

        elements.clear();
        InputElement year = new InputElement();
        year.setName(PaymentInputType.EXPIRY_YEAR);
        elements.add(year);
        assertFalse(PaymentUtils.containsExpiryDate(elements));

        elements.clear();
        elements.add(month);
        elements.add(year);
        assertTrue(PaymentUtils.containsExpiryDate(elements));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createExpiryYear_negativeYear_throwIllegalArgumentException() {
        PaymentUtils.createExpiryYear(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createExpiryYear_invalidYear_throwIllegalArgumentException() {
        PaymentUtils.createExpiryYear(100);
    }

    @Test
    public void createExpiryYear_validYear_success() {
        int year = PaymentUtils.createExpiryYear(40);
        assertEquals(2040, year);
    }

    @Test
    public void createExpiryYear_expiredYear_success() {
        int year = PaymentUtils.createExpiryYear(22);
        assertEquals(2022, year);
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
    public void getParameterValue() {
        assertNull(PaymentUtils.getParameterValue(null, null));
        assertNull(PaymentUtils.getParameterValue("foo", null));

        List<Parameter> params = new ArrayList<>();
        assertNull(PaymentUtils.getParameterValue(null, params));
        assertNull(PaymentUtils.getParameterValue("foo", params));

        Parameter param = new Parameter();
        param.setName("name");
        param.setValue("value");
        params.add(param);

        String value = PaymentUtils.getParameterValue("name", params);
        assertEquals(param.getValue(), value);
    }

    @Test
    public void getSelfURL() throws MalformedURLException {
        ListResult listResult = new ListResult();
        assertNull(PaymentUtils.getSelfURL(listResult));

        Map<String, URL> links = new HashMap<>();
        listResult.setLinks(links);
        assertNull(PaymentUtils.getSelfURL(listResult));

        URL url = new URL("http://localhost");
        links.put("self", url);
        assertNotNull(PaymentUtils.getSelfURL(listResult));
    }

    @Test
    public void getURL() throws MalformedURLException {
        assertNull(PaymentUtils.getURL(null, null));
        assertNull(PaymentUtils.getURL("foo", null));

        Map<String, URL> urls = new HashMap<>();
        assertNull(PaymentUtils.getURL(null, urls));
        assertNull(PaymentUtils.getURL("foo", urls));

        URL url = new URL("http://localhost");
        urls.put("url", url);
        assertNull(PaymentUtils.getURL("foo", urls));
        assertNotNull(PaymentUtils.getURL("url", urls));
    }

    @Test
    public void getApplicableNetwork() {
    }

    @Test
    public void getCustomerRegistrationId() {
    }

    @Test
    public void putProviderRequests() {
    }

    @Test
    public void getProviderRequestIndex() {
    }

    @Test
    public void getProviderParameterValue() {
    }

    @Test
    public void getProviderCode() {
    }

    @Test
    public void containsRedirectType() {
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
    public void readRawResource() {
    }





}
