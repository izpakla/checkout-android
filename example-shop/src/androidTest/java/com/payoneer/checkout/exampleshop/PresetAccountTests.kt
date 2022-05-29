/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */
package com.payoneer.checkout.exampleshop

import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.payoneer.checkout.exampleshop.settings.SettingsActivity
import com.payoneer.checkout.model.NetworkOperationType
import com.payoneer.checkout.sharedtest.checkout.PaymentListHelper
import com.payoneer.checkout.sharedtest.view.PaymentMatchers
import org.hamcrest.Matcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class PresetAccountTests : AbstractTest() {

    @get:Rule
    val settingsActivityRule = ActivityTestRule(SettingsActivity::class.java)

    @Test
    fun testPresetAccountWithoutAccountMask() {
        val presetCardIndex = 1
        val networkCardIndex = 3
        val checkoutActivity = openCheckoutActivity(NetworkOperationType.PRESET)
        val checkoutResultHandledIdlingResource = checkoutActivity.getResultHandledIdlingResource()
        clickCheckoutButton()
        PaymentListHelper.waitForPaymentListLoaded(1)
        PaymentListHelper.openPaymentListCard(networkCardIndex, "card.network")
        PaymentListHelper.clickPaymentListCardButton(networkCardIndex)

        register(checkoutResultHandledIdlingResource)
        waitForSummaryActivityLoaded()
        unregister(checkoutResultHandledIdlingResource)

        Espresso.onView(withId(R.id.label_title))
            .check(ViewAssertions.matches(ViewMatchers.withText("PAYPAL")))
        clickSummaryEditButton()

        PaymentListHelper.waitForPaymentListLoaded(2)
        PaymentListHelper.matchesPaymentCardTitle(presetCardIndex, "PayPal");
    }
}