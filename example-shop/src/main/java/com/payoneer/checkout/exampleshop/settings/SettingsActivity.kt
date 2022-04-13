/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */
package com.payoneer.checkout.exampleshop.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.payoneer.checkout.CheckoutConfiguration
import com.payoneer.checkout.exampleshop.R
import com.payoneer.checkout.exampleshop.checkout.CheckoutActivity.Companion.createStartIntent
import com.payoneer.checkout.exampleshop.databinding.ActivitySettingsBinding
import com.payoneer.checkout.exampleshop.shared.BaseActivity
import java.net.MalformedURLException
import java.net.URL

/**
 * This is the main Activity of this shop app in which users can paste a listUrl and start the shop.
 */
class SettingsActivity : BaseActivity() {

    private lateinit var editTextListInput: EditText
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        editTextListInput = binding.inputListurl
        binding.buttonSettings.setOnClickListener { onButtonClicked() }
    }

    private fun onButtonClicked() {
        closeKeyboard()
        try {
            val stringUrl = editTextListInput.text.toString().trim { it <= ' ' }

            val listURL = URL(stringUrl)
            val configuration = CheckoutConfiguration.createBuilder(listURL).build()
            val intent = createStartIntent(this, configuration)
            startActivity(intent)
        } catch (urlException: MalformedURLException) {
            Log.e(TAG, "createCheckoutConfigurationKotlin - Error creating URL", urlException)
            showErrorDialog()
        }
    }

    private fun showErrorDialog() =
        MaterialAlertDialogBuilder(this).apply {
            setTitle(R.string.dialog_error_title)
            setMessage(R.string.dialog_error_listurl_invalid)
            setPositiveButton(getString(R.string.dialog_error_button), null)
        }.create().show()

    private fun closeKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.let { manager ->
            val binder = editTextListInput.windowToken
            manager.hideSoftInputFromWindow(binder, 0)
        }
    }

    companion object {
        private const val TAG = "SettingsActivity"

        /**
         * Create an Intent to launch this settings activity
         *
         * @param context base for creating the start intent
         * @return the newly created intent
         */
        fun createStartIntent(context: Context?): Intent {
            requireNotNull(context) { "context may not be null" }
            val intent = Intent(context, SettingsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            return intent
        }
    }
}