/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */
package com.payoneer.checkout.exampleshop.checkout

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.res.ResourcesCompat
import com.payoneer.checkout.Checkout
import com.payoneer.checkout.CheckoutConfiguration
import com.payoneer.checkout.exampleshop.R
import com.payoneer.checkout.exampleshop.confirm.ConfirmActivity
import com.payoneer.checkout.exampleshop.databinding.ActivityCheckoutBinding
import com.payoneer.checkout.exampleshop.shared.BaseActivity
import com.payoneer.checkout.exampleshop.summary.SummaryActivity
import dagger.hilt.android.AndroidEntryPoint

/**
 * Activity displaying the checkout page, this page will open payment page of the Checkout SDK.
 */
@AndroidEntryPoint
class CheckoutActivity : BaseActivity() {

    private val checkoutViewModel by viewModels<CheckoutViewModel>()
    private lateinit var binding: ActivityCheckoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initToolbar()
        observeViewModel()
        binding.buttonCheckout.setOnClickListener { onButtonClicked() }
    }

    private fun initToolbar() {
        setSupportActionBar(binding.layoutHeader.toolbar)
        supportActionBar!!.apply {
            setTitle(R.string.checkout_collapsed_title)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        val typeface = ResourcesCompat.getFont(this, R.font.roboto_medium)
        binding.layoutHeader.collapsingToolbar.setCollapsedTitleTypeface(typeface)
        binding.layoutHeader.collapsingToolbar.setExpandedTitleTypeface(typeface)
    }

    private fun observeViewModel() {
        checkoutViewModel.showPaymentSummary.observe(this) {
            it.getIfNotHandled()?.let {
                val intent = SummaryActivity.createStartIntent(this, checkoutConfiguration)
                startActivity(intent)
                setResultHandledIdleState()
            }
        }
        checkoutViewModel.showPaymentConfirmation.observe(this) {
            it.getIfNotHandled()?.let {
                val intent = ConfirmActivity.createStartIntent(this)
                startActivity(intent)
                setResultHandledIdleState()
            }
        }
        checkoutViewModel.stopPaymentWithErrorMessage.observe(this) {
            it.getIfNotHandled()?.let {
                showErrorDialog(R.string.dialog_error_message)
            }
        }
    }

    private fun onButtonClicked() {
        val checkout = Checkout.with(checkoutConfiguration)
        checkout.showPaymentList(this, PAYMENT_REQUEST_CODE)
    }

    override fun onResume() {
        super.onResume()
        if (activityResult != null) {
            checkoutViewModel.handleCheckoutActivityResult(activityResult!!)
            activityResult = null
        }
    }

    override fun onErrorDialogClosed() {
        supportFinishAfterTransition()
    }

    companion object {
        /**
         * Create an Intent to launch this checkout activity
         *
         * @param context the context
         * @param listUrl url of the current list
         * @return the newly created intent
         */
        @JvmStatic
        fun createStartIntent(context: Context?, checkoutConfiguration: CheckoutConfiguration?): Intent {
            requireNotNull(context) { "context may not be null" }
            requireNotNull(checkoutConfiguration) { "checkoutConfiguration cannot be null" }
            val intent = Intent(context, CheckoutActivity::class.java)
            intent.putExtra(EXTRA_CHECKOUT_CONFIGURATION, checkoutConfiguration)
            return intent
        }
    }
}