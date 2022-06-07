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

import com.payoneer.checkout.sharedtest.checkout.PaymentListHelper;
import com.payoneer.checkout.sharedtest.service.ListSettings;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

@RunWith(AndroidJUnit4.class)
@LargeTest
public final class ExtraElementTests extends BaseKotlinTest {

    private final static String DIVISION = "ExtraElements";
    private final static String EXTRAELEMENTS_BOTTOM_CONFIG = "UITests-ExtraElements-Bottom";
    private final static String EXTRAELEMENTS_TOP_CONFIG = "UITests-ExtraElements-Top";
    private final static String EXTRAELEMENTS_TOPBOTTOM_CONFIG = "UITests-ExtraElements-TopBottom";

    @Test
    public void testPaymentList_notReloaded() {
        ListSettings settings = createDefaultListSettings();
        settings.setDivision(DIVISION);
        settings.setCheckoutConfigurationName(EXTRAELEMENTS_BOTTOM_CONFIG);
        enterListUrl(createListUrl(settings));
        clickShowPaymentListButton();

        int networkCardIndex = 2;
        PaymentListHelper.waitForPaymentListLoaded(1);
        PaymentListHelper.openPaymentListCard(networkCardIndex, "card.network");

        PaymentListHelper.clickExtraElementLinkWithText(networkCardIndex, "extraelement.bottomelement2", "Number 2");
        clickBrowserPageButton("two", CHROME_CLOSE_BUTTON);

        waitForAppRelaunch();
        PaymentListHelper.checkIsPaymentCardExpanded(networkCardIndex);
    }

    @Test
    public void testGroupedNetworks_topElement_clickLink() {
        ListSettings settings = createDefaultListSettings();
        settings.setDivision(DIVISION);
        settings.setCheckoutConfigurationName(EXTRAELEMENTS_TOP_CONFIG);
        enterListUrl(createListUrl(settings));
        clickShowPaymentListButton();

        int networkCardIndex = 1;
        PaymentListHelper.waitForPaymentListLoaded(1);
        PaymentListHelper.openPaymentListCard(networkCardIndex, "card.group");

        PaymentListHelper.clickExtraElementLinkWithText(networkCardIndex, "extraelement.topelement1", "Number 1");
        clickBrowserPageButton("one", CHROME_CLOSE_BUTTON);
    }

    @Test
    public void testSingleNetwork_bottomElement_clickLink() {
        ListSettings settings = createDefaultListSettings();
        settings.setDivision(DIVISION);
        settings.setCheckoutConfigurationName(EXTRAELEMENTS_BOTTOM_CONFIG);
        enterListUrl(createListUrl(settings));
        clickShowPaymentListButton();

        int networkCardIndex = 2;
        PaymentListHelper.waitForPaymentListLoaded(1);
        PaymentListHelper.openPaymentListCard(networkCardIndex, "card.network");

        PaymentListHelper.clickExtraElementLinkWithText(networkCardIndex, "extraelement.bottomelement2", "Number 2");
        clickBrowserPageButton("two", CHROME_CLOSE_BUTTON);
    }

    @Test
    public void testSingleNetwork_bottomElement_clickBothLinks() {
        ListSettings settings = createDefaultListSettings();
        settings.setDivision(DIVISION);
        settings.setCheckoutConfigurationName(EXTRAELEMENTS_BOTTOM_CONFIG);
        enterListUrl(createListUrl(settings));
        clickShowPaymentListButton();

        int networkCardIndex = 2;
        PaymentListHelper.waitForPaymentListLoaded(1);
        PaymentListHelper.openPaymentListCard(networkCardIndex, "card.network");

        PaymentListHelper.clickExtraElementLinkWithText(networkCardIndex, "extraelement.bottomelement3", "Number3A");
        clickBrowserPageButton("3A", CHROME_CLOSE_BUTTON);
        waitForAppRelaunch();
        PaymentListHelper.clickExtraElementLinkWithText(networkCardIndex, "extraelement.bottomelement3", "Number3B");
        clickBrowserPageButton("3B", CHROME_CLOSE_BUTTON);
    }

    @Test
    public void testSingleNetwork_topBottomElements_clickTopBottomLinks() {
        ListSettings settings = createDefaultListSettings();
        settings.setDivision(DIVISION);
        settings.setCheckoutConfigurationName(EXTRAELEMENTS_TOPBOTTOM_CONFIG);
        enterListUrl(createListUrl(settings));
        clickShowPaymentListButton();

        int networkCardIndex = 2;
        PaymentListHelper.waitForPaymentListLoaded(1);
        PaymentListHelper.openPaymentListCard(networkCardIndex, "card.network");

        PaymentListHelper.clickExtraElementLinkWithText(networkCardIndex, "extraelement.topelement1", "Number 1");
        clickBrowserPageButton("one", CHROME_CLOSE_BUTTON);
        waitForAppRelaunch();
        PaymentListHelper.clickExtraElementLinkWithText(networkCardIndex, "extraelement.bottomelement2", "Number 2");
        clickBrowserPageButton("two", CHROME_CLOSE_BUTTON);
    }
}
