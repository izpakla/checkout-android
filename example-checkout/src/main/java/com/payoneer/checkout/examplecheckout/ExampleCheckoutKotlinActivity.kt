/*
 *
 *  Copyright (c) 2021 Payoneer Germany GmbH
 *  https://www.payoneer.com
 *
 *  This file is open source and available under the MIT license.
 *  See the LICENSE file for more information.
 *
 */

package com.payoneer.checkout.examplecheckout

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.payoneer.checkout.examplecheckout.databinding.ActivityExamplecheckoutBinding

class ExampleCheckoutKotlinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExamplecheckoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExamplecheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}