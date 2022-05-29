/*
 *
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 *
 */

package com.payoneer.checkout.exampleshop

import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.platform.app.InstrumentationRegistry
import com.payoneer.checkout.exampleshop.checkout.CheckoutActivity
import com.payoneer.checkout.exampleshop.confirm.ConfirmActivity
import com.payoneer.checkout.exampleshop.summary.SummaryActivity
import com.payoneer.checkout.sharedtest.checkout.PaymentListHelper
import com.payoneer.checkout.sharedtest.checkout.TestDataProvider
import com.payoneer.checkout.sharedtest.service.ListService
import com.payoneer.checkout.sharedtest.service.ListSettings
import com.payoneer.checkout.sharedtest.view.ActivityHelper
import com.payoneer.checkout.sharedtest.view.PaymentActions
import com.payoneer.checkout.ui.screen.payment.ProcessPaymentActivity
import org.junit.After
import org.junit.Before
import java.net.MalformedURLException
import java.net.URL

open class AbstractTest {

    @Before
    fun beforeTest() {
        Intents.init()
    }

    @After
    fun afterTest() {
        Intents.release()
    }

    fun openCheckoutActivity(operationType: String): CheckoutActivity {
        val listUrl = createListUrl(operationType)
        Espresso.onView(withId(R.id.layout_settings))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.input_listurl)).perform(ViewActions.typeText(listUrl))
        Espresso.onView(withId(R.id.button_settings)).perform(ViewActions.click())
        Intents.intended(IntentMatchers.hasComponent(CheckoutActivity::class.java.name))
        return ActivityHelper.getCurrentActivity() as CheckoutActivity
    }

    fun clickCheckoutButton() {
        Intents.intended(IntentMatchers.hasComponent(CheckoutActivity::class.java.name))
        Espresso.onView(withId(R.id.button_checkout))
            .perform(PaymentActions.scrollToView(), ViewActions.click())
    }

    fun fillPaymentListCardData(cardIndex: Int) {
        val cardData = TestDataProvider.visaCardTestData()
        PaymentListHelper.fillPaymentListCard(cardIndex, cardData)
    }

    fun waitForSummaryActivityLoaded(): SummaryActivity {
        Intents.intended(IntentMatchers.hasComponent(SummaryActivity::class.java.name))
        val summaryActivity = ActivityHelper.getCurrentActivity() as SummaryActivity
        val loadIdlingResource = summaryActivity.getLoadIdlingResource()
        register(loadIdlingResource)

        Espresso.onView(withId(R.id.layout_coordinator))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        unregister(loadIdlingResource)
        return summaryActivity
    }

    fun clickSummaryPayButton() {
        Intents.intended(IntentMatchers.hasComponent(SummaryActivity::class.java.name))
        Espresso.onView(withId(R.id.button_pay))
            .perform(PaymentActions.scrollToView(), ViewActions.click())
    }

    fun clickSummaryEditButton() {
        Intents.intended(IntentMatchers.hasComponent(SummaryActivity::class.java.name))
        Espresso.onView(withId(R.id.button_edit))
            .perform(PaymentActions.scrollToView(), ViewActions.click())
    }

    fun waitForConfirmActivityLoaded(resultHandledIdlingResource: IdlingResource?): ConfirmActivity? {
        register(resultHandledIdlingResource)
        Intents.intended(IntentMatchers.hasComponent(ConfirmActivity::class.java.name))
        Espresso.onView(withId(R.id.layout_confirm))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        return ActivityHelper.getCurrentActivity() as ConfirmActivity
    }

    fun register(resource: IdlingResource?) {
        IdlingRegistry.getInstance().register(resource)
    }

    fun unregister(resource: IdlingResource?) {
        IdlingRegistry.getInstance().unregister(resource)
    }

    private fun createListUrl(operationType: String): String {
        val paymentApiListUrl = BuildConfig.paymentApiListUrl
        val merchantCode = BuildConfig.merchantCode
        val merchantPaymentToken = BuildConfig.merchantPaymentToken
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val settings = ListSettings(com.payoneer.checkout.exampleshop.test.R.raw.listtemplate)
            .setAppId(context.packageName)
            .setOperationType(operationType)
        val service =
            ListService.createInstance(createListURL(paymentApiListUrl), merchantCode, merchantPaymentToken)
        return service.newListSelfUrl(settings)
    }

    private fun createListURL(stringUrl: String) =
        try {
            URL(stringUrl)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            null
        }
}