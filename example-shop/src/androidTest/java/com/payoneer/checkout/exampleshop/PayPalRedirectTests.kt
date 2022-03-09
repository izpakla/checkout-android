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
import com.payoneer.checkout.exampleshop.settings.SettingsActivity
import com.payoneer.checkout.model.NetworkOperationType
import com.payoneer.checkout.sharedtest.checkout.PaymentListHelper
import com.payoneer.checkout.sharedtest.view.UiDeviceHelper
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class PayPalRedirectTests : AbstractTest() {

    @get:Rule
    val settingsActivityRule = ActivityTestRule(SettingsActivity::class.java)

    @Test
    fun testPayPalRedirect_directCharge_customerAccept() {
        val networkCardIndex = 3
        val checkoutActivity = openCheckoutActivity(NetworkOperationType.CHARGE)
        val resultHandledIdlingResource = checkoutActivity.getResultHandledIdlingResource()
        clickCheckoutButton()
        PaymentListHelper.waitForPaymentListLoaded(1)
        PaymentListHelper.openPaymentListCard(networkCardIndex, "card.network")
        PaymentListHelper.clickPaymentListCardButton(networkCardIndex)
        clickDecisionPageButton("customer-accept")
        waitForConfirmActivityLoaded(resultHandledIdlingResource)
        unregister(resultHandledIdlingResource)
    }

    @Test
    fun testPayPalRedirect_presetFlow_customerAccept() {
        val networkCardIndex = 3
        val checkoutActivity = openCheckoutActivity(NetworkOperationType.PRESET)
        val checkoutPaymentResultIdlingResource = checkoutActivity.getResultHandledIdlingResource()
        clickCheckoutButton()
        PaymentListHelper.waitForPaymentListLoaded(1)
        PaymentListHelper.openPaymentListCard(networkCardIndex, "card.network")
        PaymentListHelper.clickPaymentListCardButton(networkCardIndex)
        register(checkoutPaymentResultIdlingResource)
        waitForSummaryActivityLoaded()
        unregister(checkoutPaymentResultIdlingResource)
        val summaryActivity = waitForSummaryActivityLoaded()
        val summaryPaymentResultIdlingResource = summaryActivity.getResultHandledIdlingResource()
        clickSummaryPayButton()
        clickDecisionPageButton("customer-accept")
        waitForConfirmActivityLoaded(summaryPaymentResultIdlingResource)
        unregister(summaryPaymentResultIdlingResource)
    }

    private fun clickDecisionPageButton(buttonId: String) {
        UiDeviceHelper.checkUiObjectContainsText("customer decision page")
        UiDeviceHelper.clickUiObjectByResourceName(buttonId)
        UiDeviceHelper.waitUiObjectHasPackage("com.payoneer.checkout.exampleshop")
    }
}