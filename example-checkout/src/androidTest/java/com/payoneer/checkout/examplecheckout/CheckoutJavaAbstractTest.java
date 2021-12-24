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

import com.payoneer.checkout.sharedtest.view.ActivityHelper;

import androidx.test.espresso.IdlingResource;

public class CheckoutJavaAbstractTest extends AbstractTest {

    @Override
    public IdlingResource getResultIdlingResource() {
        ExampleCheckoutJavaActivity activity = (ExampleCheckoutJavaActivity) ActivityHelper.getCurrentActivity();
        return activity.getResultHandledIdlingResource();
    }
}
