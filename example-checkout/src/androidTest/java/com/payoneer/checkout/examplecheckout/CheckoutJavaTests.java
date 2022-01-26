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

import static com.payoneer.checkout.sharedtest.checkout.MagicNumbers.CHARGE_PROCEED_OK;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.payoneer.checkout.model.InteractionCode;
import com.payoneer.checkout.model.InteractionReason;
import com.payoneer.checkout.sharedtest.checkout.PaymentListHelper;
import com.payoneer.checkout.sharedtest.checkout.TestDataProvider;
import com.payoneer.checkout.sharedtest.service.ListSettings;

import androidx.test.espresso.IdlingResource;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class CheckoutJavaTests extends BaseJavaTest {

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
    public void testPayPalRedirect_directCharge_customerAccept() {
        IdlingResource resultIdlingResource = getResultIdlingResource();
        enterListUrl(createListUrl());
        clickShowPaymentListButton();

        int networkCardIndex = 3;
        PaymentListHelper.waitForPaymentListLoaded(1);
        PaymentListHelper.openPaymentListCard(networkCardIndex, "card.network");
        PaymentListHelper.clickPaymentListCardButton(networkCardIndex);

        clickCustomerDecisionPageButton("customer-accept");
        waitForAppRelaunch();

        register(resultIdlingResource);
        matchResultInteraction(InteractionCode.PROCEED, InteractionReason.OK);
        unregister(resultIdlingResource);
    }

    @Test
    public void testVisaCard_PROCEED_OK() {
        IdlingResource resultIdlingResource = getResultIdlingResource();

        ListSettings settings = createDefaultListSettings();
        settings.setAmount(CHARGE_PROCEED_OK);
        enterListUrl(createListUrl(settings));
        clickShowPaymentListButton();

        int groupCardIndex = 1;
        PaymentListHelper.waitForPaymentListLoaded(1);
        PaymentListHelper.openPaymentListCard(groupCardIndex, "card.group");
        PaymentListHelper.fillPaymentListCard(groupCardIndex, TestDataProvider.visaCardTestData());
        PaymentListHelper.clickPaymentListCardButton(groupCardIndex);

        register(resultIdlingResource);
        matchResultInteraction(InteractionCode.PROCEED, InteractionReason.OK);
        unregister(resultIdlingResource);
    }
}
