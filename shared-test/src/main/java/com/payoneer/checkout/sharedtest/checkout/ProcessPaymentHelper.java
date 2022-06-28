/*
 * Copyright (c) 2021 Payoneer Germany GmbH
 * https://payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.sharedtest.checkout;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import com.payoneer.checkout.R;
import com.payoneer.checkout.sharedtest.view.PaymentActions;
import com.payoneer.checkout.ui.screen.idlingresource.PaymentIdlingResources;
import com.payoneer.checkout.ui.screen.payment.ProcessPaymentActivity;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.matcher.ViewMatchers;

public final class ProcessPaymentHelper {

    public static void waitForProcessPaymentDialog() {
        intended(hasComponent(ProcessPaymentActivity.class.getName()));
        ProcessPaymentActivity paymentActivity = (ProcessPaymentActivity) PaymentActions.getActivityWithClass(ProcessPaymentActivity.class);
        PaymentIdlingResources idlingResources = paymentActivity.getPaymentIdlingResources();
        IdlingResource dialogIdlingResource = idlingResources.getDialogIdlingResource();

        IdlingRegistry.getInstance().register(dialogIdlingResource);
        onView(ViewMatchers.withId(R.id.alertTitle)).
            inRoot(withDecorView(not(is(paymentActivity.getWindow().getDecorView())))).
            check(matches(isDisplayed()));

        idlingResources.resetDialogIdlingResource();
        IdlingRegistry.getInstance().unregister(dialogIdlingResource);
    }
}
