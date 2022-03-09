/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */
package com.payoneer.checkout.exampleshop.confirm

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.payoneer.checkout.exampleshop.databinding.ActivityConfirmBinding
import com.payoneer.checkout.exampleshop.settings.SettingsActivity
import com.payoneer.checkout.exampleshop.shared.BaseActivity

/**
 * This is the confirm screen shown after a charge operation has been completed.
 */
class ConfirmActivity : BaseActivity() {

    private lateinit var binding: ActivityConfirmBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfirmBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.buttonNeworder.setOnClickListener { openSettingsScreen() }
    }

    override fun onBackPressed() {
        openSettingsScreen()
    }

    private fun openSettingsScreen() {
        val intent = SettingsActivity.createStartIntent(this)
        startActivity(intent)
    }

    companion object {
        /**
         * Create an Intent to launch this confirm activity
         *
         * @return the newly created intent
         */
        fun createStartIntent(context: Context?): Intent {
            requireNotNull(context) { "context may not be null" }
            return Intent(context, ConfirmActivity::class.java)
        }
    }
}