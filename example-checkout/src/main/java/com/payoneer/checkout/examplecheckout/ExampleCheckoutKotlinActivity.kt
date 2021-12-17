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
import android.util.Patterns
import android.view.inputmethod.InputMethodManager
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.test.espresso.IdlingResource
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.payoneer.checkout.examplecheckout.databinding.ActivityExamplecheckoutBinding
import com.payoneer.checkout.examplecheckout.utils.CHARGE_PRESET_ACCOUNT_REQUEST_CODE
import com.payoneer.checkout.examplecheckout.utils.PAYMENT_REQUEST_CODE
import com.payoneer.checkout.examplecheckout.utils.setLabel
import com.payoneer.checkout.ui.PaymentActivityResult
import com.payoneer.checkout.ui.PaymentTheme
import com.payoneer.checkout.ui.PaymentUI
import com.payoneer.checkout.ui.page.idlingresource.SimpleIdlingResource

/**
 * This is the main Activity of this example app demonstrating how to use the Checkout SDK in kotlin
 */
class ExampleCheckoutKotlinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExamplecheckoutBinding
    private val paymentUI = PaymentUI.getInstance()
    private var activityResult: PaymentActivityResult? = null
    private var resultHandledIdlingResource: SimpleIdlingResource? = null
    private var resultHandled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExamplecheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.buttonShowPaymentList.setOnClickListener { openPaymentPage() }
        binding.buttonChargePresetAcount.setOnClickListener { chargePresetAccount() }
    }

    override fun onResume() {
        super.onResume()
        resultHandled = false
        if (activityResult != null) {
            showPaymentActivityResult(activityResult)
            setResultHandledIdleState(true)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PAYMENT_REQUEST_CODE || requestCode == CHARGE_PRESET_ACCOUNT_REQUEST_CODE) {
            activityResult = PaymentActivityResult.fromActivityResult(requestCode, resultCode, data)
        }
    }

    private fun openPaymentPage() {
        if (!setListUrl()) {
            return
        }
        closeKeyboard()
        clearPaymentResult()
        paymentUI.paymentTheme = createPaymentTheme()

        // Uncomment if you like to fix e.g. the orientation to landscape mode
        // paymentUI.setOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        paymentUI.showPaymentPage(this, PAYMENT_REQUEST_CODE)
    }

    private fun chargePresetAccount() {
        if (!setListUrl()) {
            return
        }
        closeKeyboard()
        clearPaymentResult()
        paymentUI.chargePresetAccount(this, CHARGE_PRESET_ACCOUNT_REQUEST_CODE)
    }

    private fun setListUrl(): Boolean {
        val listUrl: String = binding.inputListurl.text.toString().trim { it <= ' ' }
        return if (listUrl.isNotEmpty() || Patterns.WEB_URL.matcher(listUrl).matches()) {
            paymentUI.listUrl = listUrl
            true
        } else {
            showErrorDialog(getString(R.string.dialog_error_listurl_invalid))
            false
        }
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

    private fun clearPaymentResult() {
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

    private fun createPaymentTheme(): PaymentTheme? = if (binding.switchTheme.isChecked) {
        PaymentTheme.createBuilder().setPaymentListTheme(R.style.CustomTheme_Toolbar)
            .setChargePaymentTheme(R.style.CustomTheme_NoToolbar).build()
    } else {
        PaymentTheme.createDefault()
    }

    private fun showPaymentActivityResult(sdkResult: PaymentActivityResult?) {
        if (sdkResult != null) {
            val resultCode = sdkResult.resultCode
            val paymentResult = sdkResult.paymentResult

            val info = paymentResult.resultInfo
            val interaction = paymentResult.interaction
            val code = interaction.code
            val reason = interaction.reason
            val cause = paymentResult.cause
            val error = cause.message ?: ""

            binding.apply {
                labelResultheader.isVisible = true
                layoutResult.isVisible = true
                textResultinfo.setLabel(info)
                textInteractioncode.setLabel(code)
                textInteractionreason.setLabel(reason)
                textPaymenterror.setLabel(error)
                textResultcode.setLabel(PaymentActivityResult.resultCodeToString(resultCode))
            }
        }
    }

    /**
     * Only called from test, creates and returns a new paymentResult handled IdlingResource
     */
    @VisibleForTesting
    fun getResultHandledIdlingResource(): IdlingResource {
        if (resultHandledIdlingResource == null) {
            resultHandledIdlingResource = SimpleIdlingResource(javaClass.simpleName + "-resultHandledIdlingResource")
        }
        if (resultHandled) {
            resultHandledIdlingResource?.setIdleState(true)
        } else {
            resultHandledIdlingResource?.reset()
        }
        return resultHandledIdlingResource as SimpleIdlingResource
    }
}