/*
 *
 *  * Copyright (c) 2021 Payoneer Germany GmbH
 *  * https://www.payoneer.com
 *  *
 *  * This file is open source and available under the MIT license.
 *  * See the LICENSE file for more information.
 *  *
 */

package com.payoneer.checkout.examplecheckout;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.payoneer.checkout.model.InteractionCode;
import com.payoneer.checkout.model.InteractionReason;
import com.payoneer.checkout.sharedtest.checkout.ProcessPaymentHelper;
import com.payoneer.checkout.sharedtest.checkout.MagicNumbers;
import com.payoneer.checkout.sharedtest.checkout.PaymentDialogHelper;
import com.payoneer.checkout.sharedtest.checkout.PaymentListHelper;
import com.payoneer.checkout.sharedtest.checkout.TestDataProvider;
import com.payoneer.checkout.sharedtest.service.ListSettings;

import androidx.test.espresso.IdlingResource;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

@RunWith(AndroidJUnit4.class)
@LargeTest
public final class Three3DS2Tests extends BaseKotlinTest {

    @Test
    public void test3DS2FrictionlessFlow_success() {
        IdlingResource resultIdlingResource = getResultIdlingResource();
        ListSettings settings = createDefaultListSettings();
        settings.setAmount(MagicNumbers.THREE3DS2);
        enterListUrl(createListUrl(settings));
        clickShowPaymentListButton();

        int groupCardIndex = 1;
        PaymentListHelper.waitForPaymentListLoaded(1);
        PaymentListHelper.openPaymentListCard(groupCardIndex, "card.group");
        PaymentListHelper.fillPaymentListCard(groupCardIndex, TestDataProvider.visaCardTestData());
        PaymentListHelper.clickPaymentListCardButton(groupCardIndex);

        clickDeviceCollectionPagePageButton("customer-accept");
        waitForAppRelaunch();

        register(resultIdlingResource);
        matchResultInteraction(InteractionCode.PROCEED, InteractionReason.OK);
        unregister(resultIdlingResource);
    }

    @Test
    public void test3DS2Challenge_customerAccept() {
        IdlingResource resultIdlingResource = getResultIdlingResource();
        ListSettings settings = createDefaultListSettings();
        settings.setAmount(MagicNumbers.THREE3DS2);
        enterListUrl(createListUrl(settings));
        clickShowPaymentListButton();

        int groupCardIndex = 1;
        PaymentListHelper.waitForPaymentListLoaded(1);
        PaymentListHelper.openPaymentListCard(groupCardIndex, "card.group");
        PaymentListHelper.fillPaymentListCard(groupCardIndex, TestDataProvider.visaCardTestData());
        PaymentListHelper.clickPaymentListCardButton(groupCardIndex);

        clickDeviceCollectionPagePageButton("customer-abort");
        clickCustomerDecisionPageButton("customer-accept");
        waitForAppRelaunch();

        register(resultIdlingResource);
        matchResultInteraction(InteractionCode.PROCEED, InteractionReason.OK);
        unregister(resultIdlingResource);
    }

    @Test
    public void test3DS2Challenge_customerAbort() {
        ListSettings settings = createDefaultListSettings();
        settings.setAmount(MagicNumbers.THREE3DS2);
        enterListUrl(createListUrl(settings));
        clickShowPaymentListButton();

        int groupCardIndex = 1;
        PaymentListHelper.waitForPaymentListLoaded(1);
        PaymentListHelper.openPaymentListCard(groupCardIndex, "card.group");
        PaymentListHelper.matchesCardGroupCount(groupCardIndex, 3);
        PaymentListHelper.fillPaymentListCard(groupCardIndex, TestDataProvider.visaCardTestData());
        PaymentListHelper.clickPaymentListCardButton(groupCardIndex);

        clickDeviceCollectionPagePageButton("customer-abort");
        clickCustomerDecisionPageButton("customer-abort");
        waitForAppRelaunch();

        PaymentListHelper.waitForPaymentListDialog();
        PaymentDialogHelper.clickPaymentDialogButton("OK");

        PaymentListHelper.waitForPaymentListLoaded(1);
        PaymentListHelper.openPaymentListCard(groupCardIndex, "card.group");
        PaymentListHelper.matchesCardGroupCount(groupCardIndex, 3);
    }
}
