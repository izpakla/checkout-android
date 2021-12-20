/*
 *
 * Copyright (c) 2021 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 *
 */

package com.payoneer.checkout.examplecheckout.kotlin

import androidx.test.espresso.IdlingResource
import com.payoneer.checkout.examplecheckout.AbstractTest
import com.payoneer.checkout.examplecheckout.ExampleCheckoutKotlinActivity
import com.payoneer.checkout.sharedtest.view.ActivityHelper

abstract class KotlinAbstractTest : AbstractTest() {

    override fun getResultIdlingResource(): IdlingResource {
        val exampleCheckoutKotlinActivity = ActivityHelper.getCurrentActivity() as ExampleCheckoutKotlinActivity
        return exampleCheckoutKotlinActivity.getResultHandledIdlingResource()
    }
}