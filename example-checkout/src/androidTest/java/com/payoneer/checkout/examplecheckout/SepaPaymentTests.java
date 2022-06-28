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
import com.payoneer.checkout.sharedtest.checkout.PaymentListHelper;
import com.payoneer.checkout.sharedtest.checkout.TestDataProvider;

import androidx.test.espresso.IdlingResource;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

@RunWith(AndroidJUnit4.class)
@LargeTest
public final class SepaPaymentTests extends BaseKotlinTest {

    @Test
    public void testSepa_PROCEED_SCHEDULED() {
        IdlingResource resultIdlingResource = getResultIdlingResource();
        enterListUrl(createListUrl());
        clickShowPaymentListButton();

        int cardIndex = 2;
        PaymentListHelper.waitForPaymentListLoaded(1);
        PaymentListHelper.openPaymentListCard(cardIndex, "card.network");
        PaymentListHelper.fillPaymentListCard(cardIndex, TestDataProvider.sepaTestData());
        PaymentListHelper.clickPaymentListCardButton(cardIndex);

        register(resultIdlingResource);
        matchResultInteraction(InteractionCode.PROCEED, InteractionReason.SCHEDULED);
        unregister(resultIdlingResource);
    }

    @Test
    public void testSepa_registration_test() {
        enterListUrl(createListUrl());
        clickShowPaymentListButton();

        int cardIndex = 2;
        PaymentListHelper.waitForPaymentListLoaded(1);
        PaymentListHelper.openPaymentListCard(cardIndex, "card.network");

        PaymentListHelper.checkHasVisibleCheckboxInWidget(cardIndex, "registration.registrationOptions");
        PaymentListHelper.matchesCheckboxInWidget(cardIndex, "registration.registrationOptions", false);
        PaymentListHelper.clickCheckboxInWidget(cardIndex, "registration.registrationOptions");
        PaymentListHelper.matchesCheckboxInWidget(cardIndex, "registration.registrationOptions", true);
    }
}

