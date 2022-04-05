package com.payoneer.checkout.examplecheckout;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ExampleCheckoutJavaActivityTest {

    @SuppressWarnings("deprecation")
    @Rule
    public ActivityTestRule<ExampleCheckoutJavaActivity> rule = new ActivityTestRule<>(ExampleCheckoutJavaActivity.class);

    @Test
    public void clickingListButtonWithEmptyUrlConfirmErrorDialogShown() {
        onView(withId(R.id.button_show_payment_list)).perform(click());

        onView(withText("Error"))
            .check(matches(isDisplayed()));
    }

    @Test
    public void clickingPresetButtonWithEmptyUrlConfirmErrorDialogShown() {
        onView(withId(R.id.button_charge_preset_acount)).perform(click());

        onView(withText("Error"))
            .check(matches(isDisplayed()));
    }

    @Test
    public void clickingListButtonWithInvalidUrlConfirmErrorDialogShown() {
        onView(withId(R.id.input_listurl)).perform(typeText("somelistUrl"));
        onView(withId(R.id.button_show_payment_list)).perform(click());

        onView(withText("Error"))
            .check(matches(isDisplayed()));
    }

    @Test
    public void clickingPresetButtonWithInvalidUrlConfirmErrorDialogShown() {
        onView(withId(R.id.input_listurl)).perform(typeText("somelistUrl"));
        onView(withId(R.id.button_charge_preset_acount)).perform(click());

        onView(withText("Error"))
            .check(matches(isDisplayed()));
    }
}