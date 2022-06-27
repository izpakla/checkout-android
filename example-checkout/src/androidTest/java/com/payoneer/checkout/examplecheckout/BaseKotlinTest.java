/*
 *
 * Copyright (c) 2021 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 *
 */

package com.payoneer.checkout.examplecheckout;

import static com.payoneer.checkout.sharedtest.view.PaymentActions.getActivityWithClass;

import org.junit.Rule;

import androidx.test.espresso.IdlingResource;
import androidx.test.rule.ActivityTestRule;

public abstract class BaseKotlinTest extends BaseTest {

    @SuppressWarnings("deprecation")
    @Rule
    public ActivityTestRule<ExampleCheckoutKotlinActivity> rule = new ActivityTestRule<>(ExampleCheckoutKotlinActivity.class);

    @Override
    protected IdlingResource getResultIdlingResource() {
        ExampleCheckoutKotlinActivity activity = (ExampleCheckoutKotlinActivity) getActivityWithClass(ExampleCheckoutKotlinActivity.class);
        return activity.getResultHandledIdlingResource();
    }
}
