package com.payoneer.checkout.examplecheckout

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class ExampleCheckoutKotlinActivityTest : BaseKotlinTest() {

    @Test
    fun clickingButtonWithEmptyUrlConfirmErrorDialogShown() {
        onView(ViewMatchers.withId(R.id.button_show_payment_list)).perform(click())

        onView(withText("Please paste a valid List Url in the input field."))
            .check(matches(isDisplayed()))
    }

    @Test
    fun clickingButtonWithInvalidUrlConfirmErrorDialogShown() {
        onView(ViewMatchers.withId(R.id.input_listurl)).perform(typeText("somelistUrl"))

        onView(ViewMatchers.withId(R.id.button_show_payment_list)).perform(click())

        onView(withText("Please paste a valid List Url in the input field."))
            .check(matches(isDisplayed()))
    }
}