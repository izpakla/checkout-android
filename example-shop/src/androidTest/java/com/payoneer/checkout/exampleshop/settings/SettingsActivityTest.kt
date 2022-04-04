package com.payoneer.checkout.exampleshop.settings

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.payoneer.checkout.exampleshop.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class SettingsActivityTest {

    @Suppress("DEPRECATION")
    @get:Rule
    val settingsActivityRule = ActivityTestRule(SettingsActivity::class.java)

    @Test
    fun clickingDemoButtonWithEmptyUrlConfirmErrorDialogShown() {
        onView(withId(R.id.button_settings)).perform(click())

        onView(withText("Please paste a valid List Url in the input field.")).check(matches(isDisplayed()))
    }

    @Test
    fun clickingDemoButtonWithInvalidUrlConfirmErrorDialogShown() {
        onView(withId(R.id.input_listurl)).perform(typeText("somelistUrl"))

        onView(withId(R.id.button_settings)).perform(click())

        onView(withText("Please paste a valid List Url in the input field.")).check(matches(isDisplayed()))
    }
}