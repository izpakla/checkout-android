package com.payoneer.checkout.examplecheckout

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class ExampleCheckoutKotlinActivityTest {

    @Suppress("DEPRECATION")
    @get:Rule
    val rule = ActivityTestRule(ExampleCheckoutJavaActivity::class.java)

    @Test
    fun clickingListButtonWithEmptyUrlConfirmErrorDialogShown() {
        onView(withId(R.id.button_show_payment_list)).perform(click())

        onView(withText("Error"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun clickingListButtonWithInvalidUrlConfirmErrorDialogShown() {
        onView(withId(R.id.input_listurl)).perform(typeText("somelistUrl"))

        onView(withId(R.id.button_show_payment_list)).perform(click())

        onView(withText("Error"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun clickingPresetButtonWithEmptyUrlConfirmErrorDialogShown() {
        onView(withId(R.id.button_charge_preset_acount)).perform(click())

        onView(withText("Error"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun clickingPresetButtonWithInvalidUrlConfirmErrorDialogShown() {
        onView(withId(R.id.input_listurl)).perform(typeText("somelistUrl"))

        onView(withId(R.id.button_charge_preset_acount)).perform(click())

        onView(withText("Please paste a valid List Url in the input field."))
            .check(matches(isDisplayed()))
    }
}