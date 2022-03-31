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

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.test.espresso.IdlingResource
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.payoneer.checkout.Checkout
import com.payoneer.checkout.CheckoutActivityResult
import com.payoneer.checkout.CheckoutConfiguration
import com.payoneer.checkout.CheckoutTheme
import com.payoneer.checkout.examplecheckout.databinding.ActivityExamplecheckoutBinding
import com.payoneer.checkout.ui.page.idlingresource.SimpleIdlingResource
import java.net.MalformedURLException
import java.net.URL

/**
 * This is the main Activity of this example app demonstrating how to use the Checkout SDK in kotlin
 */
class ExampleCheckoutKotlinActivity : AppCompatActivity() {

    companion object {
        const val PAYMENT_REQUEST_CODE = 1
        const val CHARGE_PRESET_ACCOUNT_REQUEST_CODE = 2
        private const val TAG = "CheckoutKotlinActivity"
    }

    private lateinit var binding: ActivityExamplecheckoutBinding
    private var activityResult: CheckoutActivityResult? = null
    private var resultHandledIdlingResource: SimpleIdlingResource? = null
    private var resultHandled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExamplecheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.buttonShowPaymentList.setOnClickListener { openPaymentList() }
        binding.buttonChargePresetAcount.setOnClickListener { chargePresetAccount() }
    }

    override fun onResume() {
        super.onResume()
        resultHandled = false
        if (activityResult != null) {
            showCheckoutActivityResult(activityResult!!)
            setResultHandledIdleState(true)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PAYMENT_REQUEST_CODE || requestCode == CHARGE_PRESET_ACCOUNT_REQUEST_CODE) {
            activityResult =
                CheckoutActivityResult.fromActivityResult(requestCode, resultCode, data)
        }
    }

    private fun openPaymentList() {
        val checkoutConfiguration = createCheckoutConfiguration() ?: return
        closeKeyboard()
        clearCheckoutResult()

        val checkout = Checkout.of(checkoutConfiguration)
        checkout.showPaymentList(this, CHARGE_PRESET_ACCOUNT_REQUEST_CODE)
    }

    private fun chargePresetAccount() {
        val checkoutConfiguration = createCheckoutConfiguration() ?: return
        closeKeyboard()
        clearCheckoutResult()

        val checkout = Checkout.of(checkoutConfiguration)
        checkout.chargePresetAccount(this, CHARGE_PRESET_ACCOUNT_REQUEST_CODE)
    }

    private fun showErrorDialog(message: String) =
        MaterialAlertDialogBuilder(this).apply {
            setTitle(R.string.dialog_error_title)
            setMessage(message)
            setPositiveButton(getString(R.string.dialog_error_button), null)
        }.create().show()

    private fun closeKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val binder = binding.inputListurl.windowToken
        imm.hideSoftInputFromWindow(binder, 0)
    }

    private fun clearCheckoutResult() {
        setResultHandledIdleState(false)
        binding.apply {
            labelResultheader.isVisible = false
            layoutResult.isVisible = false
        }
        activityResult = null
    }

    /**
     * For testing only, set the result handled idle state for the IdlingResource
     */
    private fun setResultHandledIdleState(handledState: Boolean) {
        resultHandled = handledState
        resultHandledIdlingResource?.setIdleState(handledState)
    }

    private fun createCheckoutConfiguration(): CheckoutConfiguration? {
        val stringUrl: String = binding.inputListurl.text.toString().trim()
        return try {
            val listUrl = URL(stringUrl)

            CheckoutConfiguration.createBuilder(listUrl)
                .theme(createCheckoutTheme())
                // Uncomment the following line to fix the orientation of the screens
                //.orientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
                .build()
        } catch (e: MalformedURLException) {
            Log.e(TAG, "createCheckoutConfigurationKotlin - Error creating URL")
            showErrorDialog(getString(R.string.dialog_error_listurl_invalid))
            null
        }
    }

    private fun createCheckoutTheme(): CheckoutTheme? = if (binding.switchTheme.isChecked) {
        CheckoutTheme.createBuilder().setToolbarTheme(R.style.CustomTheme_Toolbar)
            .setNoToolbarTheme(R.style.CustomTheme_NoToolbar).build()
    } else {
        CheckoutTheme.createDefault()
    }

    private fun showCheckoutActivityResult(sdkResult: CheckoutActivityResult) {
        val resultCode = sdkResult.resultCode
        val checkoutResult = sdkResult.checkoutResult

        val info = checkoutResult?.resultInfo
        val interaction = checkoutResult?.interaction
        val code = interaction?.code
        val reason = interaction?.reason
        val cause = checkoutResult?.cause
        val error = cause?.message

        binding.apply {
            labelResultheader.isVisible = true
            layoutResult.isVisible = true
            textResultinfo.setLabel(info)
            textInteractioncode.setLabel(code)
            textInteractionreason.setLabel(reason)
            textPaymenterror.setLabel(error)
            textResultcode.setLabel(CheckoutActivityResult.resultCodeToString(resultCode))
        }
    }

    private fun TextView.setLabel(message: String?) {
        val label =
            if (TextUtils.isEmpty(message)) this.context.getString(R.string.empty_label) else message
        this.text = label
    }

    /**
     * Only called from test, creates and returns a new result handled IdlingResource
     */
    @VisibleForTesting
    fun getResultHandledIdlingResource(): IdlingResource {
        if (resultHandledIdlingResource == null) {
            resultHandledIdlingResource =
                SimpleIdlingResource(javaClass.simpleName + "-resultHandledIdlingResource")
        }
        if (resultHandled) {
            resultHandledIdlingResource?.setIdleState(true)
        } else {
            resultHandledIdlingResource?.reset()
        }
        return resultHandledIdlingResource as SimpleIdlingResource
    }
}