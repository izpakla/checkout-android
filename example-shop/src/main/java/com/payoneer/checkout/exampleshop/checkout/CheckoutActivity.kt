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
import android.text.TextUtils
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.payoneer.checkout.exampleshop.R
import com.payoneer.checkout.exampleshop.checkout.CheckoutActivity
import com.payoneer.checkout.exampleshop.confirm.ConfirmActivity
import com.payoneer.checkout.exampleshop.shared.BaseActivity
import com.payoneer.checkout.exampleshop.summary.SummaryActivity
import com.payoneer.checkout.ui.PaymentUI
import dagger.hilt.android.AndroidEntryPoint

/**
 * Activity displaying the checkout page, this page will open payment page of the Checkout SDK.
 */
@AndroidEntryPoint
class CheckoutActivity : BaseActivity() {

    private val shopCheckoutViewModel by viewModels<ShopCheckoutViewModel>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)
        initToolbar()
        observeViewModel()
        findViewById<Button>(R.id.button_checkout).setOnClickListener { onButtonClicked() }
    }

    private fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar!!.setTitle(R.string.checkout_collapsed_title)
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)
        val layout = findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar)
        val typeface = ResourcesCompat.getFont(this, R.font.roboto_medium)
        layout.setCollapsedTitleTypeface(typeface)
        layout.setExpandedTitleTypeface(typeface)
    }

    private fun observeViewModel() {
        shopCheckoutViewModel.showPaymentSummary.observe(this) {
            it.getIfNotHandled()?.let {
                if (!active) {
                    return@let
                }
                val intent = SummaryActivity.createStartIntent(this, listUrl)
                startActivity(intent)
                setResultHandledIdleState()
            }
        }
        shopCheckoutViewModel.showPaymentConfirmation.observe(this) {
            it.getIfNotHandled()?.let {
                if (!active) {
                    return@let
                }
                val intent = ConfirmActivity.createStartIntent(this)
                startActivity(intent)
                setResultHandledIdleState()
            }
        }
        shopCheckoutViewModel.stopPaymentWithErrorMessage.observe(this) {
            it.getIfNotHandled()?.let {
                if (!active) {
                    return@let
                }
                showErrorDialog(R.string.dialog_error_message)
            }
        }
    }

    private fun onButtonClicked() {
        val paymentUI = PaymentUI.getInstance()
        paymentUI.listUrl = listUrl
        paymentUI.showPaymentPage(this, PAYMENT_REQUEST_CODE)
    }

    /**
     * {@inheritDoc}
     */
    override fun onResume() {
        super.onResume()
        if (activityResult != null) {
            shopCheckoutViewModel.handlePaymentActivityResult(activityResult)
            activityResult = null
        }
    }

    /**
     * {@inheritDoc}
     */
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
        fun createStartIntent(context: Context?, listUrl: String?): Intent {
            requireNotNull(context) { "context may not be null" }
            require(!TextUtils.isEmpty(listUrl)) { "listUrl may not be null or empty" }
            val intent = Intent(context, CheckoutActivity::class.java)
            intent.putExtra(EXTRA_LISTURL, listUrl)
            return intent
        }
    }
}