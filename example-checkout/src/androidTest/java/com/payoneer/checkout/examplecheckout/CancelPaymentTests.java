/*
 *
 * Copyright (c) 2021 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 *
 */

package com.payoneer.checkout.examplecheckout;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.payoneer.checkout.sharedtest.checkout.PaymentListHelper;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingResource;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class CancelPaymentTests extends BaseJavaTest {

    @Test
    public void testCancelPaymentClickActionBarUp() {
        IdlingResource resultIdlingResource = getResultIdlingResource();
        enterListUrl(createListUrl());
        clickShowPaymentListButton();

        PaymentListHelper.waitForPaymentListLoaded(1);
        pressActionBarUp();

        register(resultIdlingResource);
        matchResultCodeCanceled();
        unregister(resultIdlingResource);
    }

    @Test
    public void testCancelPaymentPressBack() {
        IdlingResource resultIdlingResource = getResultIdlingResource();
        enterListUrl(createListUrl());
        clickShowPaymentListButton();

        PaymentListHelper.waitForPaymentListLoaded(1);
        Espresso.pressBack();

        register(resultIdlingResource);
        matchResultCodeCanceled();
        unregister(resultIdlingResource);
    }
}

