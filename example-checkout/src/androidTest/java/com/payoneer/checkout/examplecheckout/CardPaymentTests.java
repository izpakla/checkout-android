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

import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static com.payoneer.checkout.sharedtest.checkout.MagicNumbers.CHARGE_PROCEED_OK;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.payoneer.checkout.model.InteractionCode;
import com.payoneer.checkout.model.InteractionReason;
import com.payoneer.checkout.model.NetworkOperationType;
import com.payoneer.checkout.sharedtest.checkout.ProcessPaymentHelper;
import com.payoneer.checkout.sharedtest.checkout.MagicNumbers;
import com.payoneer.checkout.sharedtest.checkout.PaymentDialogHelper;
import com.payoneer.checkout.sharedtest.checkout.PaymentListHelper;
import com.payoneer.checkout.sharedtest.checkout.TestDataProvider;
import com.payoneer.checkout.sharedtest.service.ListSettings;
import com.payoneer.checkout.ui.screen.list.PaymentListActivity;

import androidx.test.espresso.IdlingResource;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

@RunWith(AndroidJUnit4.class)
@LargeTest
public final class CardPaymentTests extends BaseKotlinTest {

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

    @Test
    public void testVisaCard_PROCEED_PENDING() {
        IdlingResource resultIdlingResource = getResultIdlingResource();

        ListSettings settings = createDefaultListSettings();
        settings.setAmount(MagicNumbers.CHARGE_PROCEED_PENDING);
        enterListUrl(createListUrl(settings));
        clickShowPaymentListButton();

        int groupCardIndex = 1;
        PaymentListHelper.waitForPaymentListLoaded(1);
        PaymentListHelper.openPaymentListCard(groupCardIndex, "card.group");
        PaymentListHelper.fillPaymentListCard(groupCardIndex, TestDataProvider.visaCardTestData());
        PaymentListHelper.clickPaymentListCardButton(groupCardIndex);

        register(resultIdlingResource);
        matchResultInteraction(InteractionCode.PROCEED, InteractionReason.PENDING);
        unregister(resultIdlingResource);
    }

    @Test
    public void testVisaCard_RETRY() {
        ListSettings settings = createDefaultListSettings();
        settings.setAmount(MagicNumbers.CHARGE_RETRY);
        enterListUrl(createListUrl(settings));
        clickShowPaymentListButton();

        int groupCardIndex = 1;
        PaymentListHelper.waitForPaymentListLoaded(1);
        PaymentListHelper.openPaymentListCard(groupCardIndex, "card.group");
        PaymentListHelper.matchesCardGroupCount(groupCardIndex, 3);

        PaymentListHelper.fillPaymentListCard(groupCardIndex, TestDataProvider.visaCardTestData());
        PaymentListHelper.clickPaymentListCardButton(groupCardIndex);

        PaymentListHelper.waitForPaymentListDialog();
        PaymentDialogHelper.clickPaymentDialogButton("OK");

        //PaymentListHelper.waitForPaymentListLoaded(1);
        //PaymentListHelper.matchesInputTextInWidget(groupCardIndex, "inputelement.number", "4111 1111 1111 1111");
    }

    @Test
    public void testVisaCard_TRY_OTHER_NETWORK() {
        ListSettings settings = createDefaultListSettings();
        settings.setAmount(MagicNumbers.CHARGE_TRY_OTHER_NETWORK);
        enterListUrl(createListUrl(settings));
        clickShowPaymentListButton();

        int groupCardIndex = 1;
        PaymentListHelper.waitForPaymentListLoaded(1);
        PaymentListHelper.openPaymentListCard(groupCardIndex, "card.group");
        PaymentListHelper.matchesCardGroupCount(groupCardIndex, 3);

        PaymentListHelper.fillPaymentListCard(groupCardIndex, TestDataProvider.visaCardTestData());
        PaymentListHelper.clickPaymentListCardButton(groupCardIndex);

        PaymentListHelper.waitForPaymentListDialog();
        PaymentDialogHelper.clickPaymentDialogButton("OK");

        PaymentListHelper.waitForPaymentListLoaded(1);
        PaymentListHelper.openPaymentListCard(groupCardIndex, "card.group");
        PaymentListHelper.matchesCardGroupCount(groupCardIndex, 2);
    }

    @Test
    public void testVisaCard_TRY_OTHER_ACCOUNT() {
        ListSettings settings = createDefaultListSettings();
        settings.setAmount(MagicNumbers.CHARGE_TRY_OTHER_ACCOUNT);
        enterListUrl(createListUrl(settings));
        clickShowPaymentListButton();

        int groupCardIndex = 1;

        PaymentListHelper.waitForPaymentListLoaded(1);
        PaymentListHelper.openPaymentListCard(groupCardIndex, "card.group");
        PaymentListHelper.matchesCardGroupCount(groupCardIndex, 3);

        PaymentListHelper.fillPaymentListCard(groupCardIndex, TestDataProvider.visaCardTestData());
        PaymentListHelper.clickPaymentListCardButton(groupCardIndex);

        PaymentListHelper.waitForPaymentListDialog();
        PaymentDialogHelper.clickPaymentDialogButton("OK");

        PaymentListHelper.waitForPaymentListLoaded(1);
        PaymentListHelper.openPaymentListCard(groupCardIndex, "card.group");
        PaymentListHelper.matchesCardGroupCount(groupCardIndex, 3);
    }

    @Test
    public void testRiskDeniedCard_ABORT() {
        IdlingResource resultIdlingResource = getResultIdlingResource();

        enterListUrl(createListUrl());
        clickShowPaymentListButton();

        int groupCardIndex = 1;
        PaymentListHelper.waitForPaymentListLoaded(1);
        PaymentListHelper.openPaymentListCard(groupCardIndex, "card.group");

        PaymentListHelper.fillPaymentListCard(groupCardIndex, TestDataProvider.riskDeniedCardTestData());
        PaymentListHelper.clickPaymentListCardButton(groupCardIndex);

        register(resultIdlingResource);
        matchResultInteraction(InteractionCode.ABORT, InteractionReason.RISK_DETECTED);
        unregister(resultIdlingResource);
    }

    @Test
    public void testUpdateSavedAccount_FAIL() {
        IdlingResource resultIdlingResource = getResultIdlingResource();

        ListSettings settings = createDefaultListSettings();
        String registrationId = registerExpiredAccount(settings);

        settings.setRegistrationId(registrationId);
        settings.setOperationType(NetworkOperationType.UPDATE);
        settings.setAmount(MagicNumbers.UPDATE_ABORT_SYSTEM_FAILURE);
        enterListUrl(createListUrl(settings));
        clickShowPaymentListButton();

        int accountCardIndex = 1;
        PaymentListHelper.waitForPaymentListLoaded(1);
        PaymentListHelper.openPaymentListCard(accountCardIndex, "card.account");
        PaymentListHelper.fillPaymentListCard(accountCardIndex, TestDataProvider.updateCardData());
        PaymentListHelper.clickPaymentListCardButton(accountCardIndex);

        register(resultIdlingResource);
        matchResultInteraction(InteractionCode.ABORT, InteractionReason.SYSTEM_FAILURE);
        unregister(resultIdlingResource);
    }

    @Test
    public void testGetRedirect_clickAbort_confirmWarningIsShown() {
        enterListUrl(createListUrl());
        clickShowPaymentListButton();

        int groupCardIndex = 1;
        PaymentListHelper.waitForPaymentListLoaded(1);
        PaymentListHelper.openPaymentListCard(groupCardIndex, "card.group");

        PaymentListHelper.fillPaymentListCard(groupCardIndex, TestDataProvider.getRedirectCardTestData());
        PaymentListHelper.clickPaymentListCardButton(groupCardIndex);

        clickCustomerDecisionPageButton("customer-abort");
        waitForAppRelaunch();

        PaymentListHelper.waitForPaymentListDialog();
        PaymentDialogHelper.clickPaymentDialogButton("OK");
        intended(hasComponent(PaymentListActivity.class.getName()));
    }

    @Test
    public void testGetRedirect_clickAccept_confirmWarningIsShown() {
        IdlingResource resultIdlingResource = getResultIdlingResource();

        enterListUrl(createListUrl());
        clickShowPaymentListButton();

        int groupCardIndex = 1;
        PaymentListHelper.waitForPaymentListLoaded(1);
        PaymentListHelper.openPaymentListCard(groupCardIndex, "card.group");

        PaymentListHelper.fillPaymentListCard(groupCardIndex, TestDataProvider.getRedirectCardTestData());
        PaymentListHelper.clickPaymentListCardButton(groupCardIndex);

        clickCustomerDecisionPageButton("customer-accept");
        waitForAppRelaunch();

        register(resultIdlingResource);
        matchResultInteraction(InteractionCode.PROCEED, InteractionReason.OK);
        unregister(resultIdlingResource);
    }

    @Test
    public void testPostRedirect_clickAbort_confirmWarningIsShown() {
        enterListUrl(createListUrl());
        clickShowPaymentListButton();

        int groupCardIndex = 1;
        PaymentListHelper.waitForPaymentListLoaded(1);
        PaymentListHelper.openPaymentListCard(groupCardIndex, "card.group");

        PaymentListHelper.fillPaymentListCard(groupCardIndex, TestDataProvider.postRedirectCardTestData());
        PaymentListHelper.clickPaymentListCardButton(groupCardIndex);

        clickCustomerDecisionPageButton("customer-abort");
        waitForAppRelaunch();

        PaymentListHelper.waitForPaymentListDialog();
        PaymentDialogHelper.clickPaymentDialogButton("OK");
        intended(hasComponent(PaymentListActivity.class.getName()));
    }

    @Test
    public void testPostRedirect_clickAccept_confirmWarningIsShown() {
        IdlingResource resultIdlingResource = getResultIdlingResource();

        enterListUrl(createListUrl());
        clickShowPaymentListButton();

        int groupCardIndex = 1;
        PaymentListHelper.waitForPaymentListLoaded(1);
        PaymentListHelper.openPaymentListCard(groupCardIndex, "card.group");

        PaymentListHelper.fillPaymentListCard(groupCardIndex, TestDataProvider.postRedirectCardTestData());
        PaymentListHelper.clickPaymentListCardButton(groupCardIndex);

        clickCustomerDecisionPageButton("customer-accept");
        waitForAppRelaunch();

        register(resultIdlingResource);
        matchResultInteraction(InteractionCode.PROCEED, InteractionReason.OK);
        unregister(resultIdlingResource);
    }
}

