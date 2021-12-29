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

import org.junit.Rule;

import com.payoneer.checkout.sharedtest.view.ActivityHelper;

import androidx.test.espresso.IdlingResource;
import androidx.test.rule.ActivityTestRule;

public abstract class BaseJavaTest extends BaseTest {

    @SuppressWarnings("deprecation")
    @Rule
    public ActivityTestRule<ExampleCheckoutJavaActivity> rule = new ActivityTestRule<>(ExampleCheckoutJavaActivity.class);

    protected IdlingResource getResultIdlingResource() {
        ExampleCheckoutJavaActivity activity = (ExampleCheckoutJavaActivity) ActivityHelper.getCurrentActivity();
        return activity.getResultHandledIdlingResource();
    }
}
