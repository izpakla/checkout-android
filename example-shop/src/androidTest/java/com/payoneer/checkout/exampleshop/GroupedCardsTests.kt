/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */
package com.payoneer.checkout.exampleshop

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.payoneer.checkout.exampleshop.checkout.CheckoutActivity
import com.payoneer.checkout.exampleshop.settings.SettingsActivity
import com.payoneer.checkout.exampleshop.summary.SummaryActivity
import com.payoneer.checkout.model.NetworkOperationType
import com.payoneer.checkout.sharedtest.checkout.PaymentListHelper
import com.payoneer.checkout.sharedtest.checkout.TestDataProvider
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class GroupedCardsTests : AbstractTest() {

    @get:Rule
    val settingsActivityRule = ActivityTestRule(SettingsActivity::class.java)

    @Test
    fun testVisa_directCharge_success() {
        val groupCardIndex = 1
        val checkoutActivity: CheckoutActivity = openCheckoutActivity(NetworkOperationType.CHARGE)
        val resultHandledIdlingResource = checkoutActivity.getResultHandledIdlingResource()
        clickCheckoutButton()
        PaymentListHelper.waitForPaymentListLoaded(1)
        PaymentListHelper.openPaymentListCard(groupCardIndex, "card.group")
        PaymentListHelper.fillPaymentListCard(groupCardIndex, TestDataProvider.visaCardTestData())
        PaymentListHelper.clickPaymentListCardButton(groupCardIndex)
        waitForConfirmActivityLoaded(resultHandledIdlingResource)
        unregister(resultHandledIdlingResource)
    }

    @Test
    fun testVisa_presetFlow_success() {
        val groupCardIndex = 1
        val checkoutActivity: CheckoutActivity = openCheckoutActivity(NetworkOperationType.PRESET)
        val checkoutPaymentResultIdlingResource = checkoutActivity.getResultHandledIdlingResource()
        clickCheckoutButton()
        PaymentListHelper.waitForPaymentListLoaded(1)
        PaymentListHelper.openPaymentListCard(groupCardIndex, "card.group")
        PaymentListHelper.fillPaymentListCard(groupCardIndex, TestDataProvider.visaCardTestData())
        PaymentListHelper.clickPaymentListCardButton(groupCardIndex)
        register(checkoutPaymentResultIdlingResource)
        waitForSummaryActivityLoaded()
        unregister(checkoutPaymentResultIdlingResource)
        val summaryActivity: SummaryActivity = waitForSummaryActivityLoaded()
        val summaryPaymentResultIdlingResource = summaryActivity.getResultHandledIdlingResource()
        clickSummaryPayButton()
        waitForConfirmActivityLoaded(summaryPaymentResultIdlingResource)
        unregister(summaryPaymentResultIdlingResource)
    }
}