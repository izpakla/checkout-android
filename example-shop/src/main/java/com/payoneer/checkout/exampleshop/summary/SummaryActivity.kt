/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */
package com.payoneer.checkout.exampleshop.summary

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.VisibleForTesting
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.test.espresso.IdlingResource
import com.payoneer.checkout.exampleshop.R
import com.payoneer.checkout.exampleshop.confirm.ConfirmActivity
import com.payoneer.checkout.exampleshop.databinding.ActivitySummaryBinding
import com.payoneer.checkout.exampleshop.settings.SettingsActivity
import com.payoneer.checkout.exampleshop.shared.BaseActivity
import com.payoneer.checkout.exampleshop.util.Resource
import com.payoneer.checkout.model.AccountMask
import com.payoneer.checkout.model.PaymentMethod
import com.payoneer.checkout.model.PresetAccount
import com.payoneer.checkout.ui.PaymentUI
import com.payoneer.checkout.ui.page.idlingresource.SimpleIdlingResource
import com.payoneer.checkout.util.AccountMaskUtils
import com.payoneer.checkout.util.NetworkLogoLoader

/**
 * Activity displaying the summary page with the Pay and Edit button.
 */
class SummaryActivity : BaseActivity() {
    private var presetAccount: PresetAccount? = null
    private val viewModel by viewModels<ShopSummaryViewModel>()
    private lateinit var binding: ActivitySummaryBinding
    private lateinit var presetTitle: TextView
    private lateinit var presetSubtitle: TextView

    // For automated UI Testing
    private var loadCompleted = false
    private var loadIdlingResource: SimpleIdlingResource? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initToolbar()
        initListenersAndViews()
        initObservers()
    }

    private fun initObservers() {
        viewModel.showPaymentConfirmation.observe(this) {
            it.getIfNotHandled()?.let {
                startActivity(ConfirmActivity.createStartIntent(this))
                supportFinishAfterTransition()
                setResultHandledIdleState()
            }
        }
        viewModel.stopPaymentWithErrorMessage.observe(this) {
            it.getIfNotHandled()?.let { showErrorDialog(R.string.dialog_error_message) }
        }
        viewModel.showPaymentList.observe(this) {
            it.getIfNotHandled()?.let { showPaymentList() }
        }
        viewModel.loadPaymentDetails.observe(this) {
            it.getContentIfNotHandled()?.let { load ->
                if (load) viewModel.loadPaymentDetails(listUrl)
            }
        }
        viewModel.showPaymentDetails.observe(this) { resource ->
            when (resource.status) {
                Resource.Status.SUCCESS -> showPaymentDetails(resource.data)
                Resource.Status.ERROR -> showErrorDialog(R.string.dialog_error_message)
                Resource.Status.LOADING -> showLoading()
            }
        }
    }

    private fun initListenersAndViews() {
        presetTitle = binding.layoutSummaryDetails.labelTitle
        presetSubtitle = binding.layoutSummaryDetails.labelSubtitle
        binding.layoutSummaryDetails.buttonEdit.setOnClickListener { showPaymentList() }
        binding.layoutSummaryDetails.buttonPay.setOnClickListener { onPayClicked() }
    }

    private fun initToolbar() {
        setSupportActionBar(binding.layoutHeader.toolbar)
        val actionBar = supportActionBar
        actionBar!!.apply {
            setTitle(R.string.summary_collapsed_title)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        val typeface = ResourcesCompat.getFont(this, R.font.roboto_medium)
        binding.layoutHeader.collapsingToolbar.apply {
            setCollapsedTitleTypeface(typeface)
            setExpandedTitleTypeface(typeface)
        }
    }

    override fun onResume() {
        super.onResume()
        if (activityResult != null) {
            viewModel.handlePaymentActivityResult(activityResult!!)
            activityResult = null
        } else {
            viewModel.loadPaymentDetails(listUrl)
        }
    }

    private fun showPaymentDetails(presetAccount: PresetAccount?) {
        binding.progressbarLoad.isVisible = false
        binding.layoutContent.isVisible = true
        this.presetAccount = presetAccount
        val mask = presetAccount?.maskedAccount
        val view = binding.layoutSummaryDetails.layoutViewLogo.rootView as ImageView
        val logoUrl = presetAccount?.links?.get("logo")
        val networkCode = presetAccount?.code
        NetworkLogoLoader.loadNetworkLogo(view, networkCode, logoUrl)
        presetSubtitle.visibility = View.GONE
        if (mask != null) {
            setAccountMask(mask, presetAccount.method)
        } else {
            presetTitle.text = presetAccount?.code
        }
        // For automated UI testing
        loadCompleted = true
        if (loadIdlingResource != null) {
            loadIdlingResource!!.setIdleState(loadCompleted)
        }
    }

    private fun setAccountMask(mask: AccountMask, method: String) {
        when (method) {
            PaymentMethod.CREDIT_CARD, PaymentMethod.DEBIT_CARD -> {
                presetTitle.text = mask.number
                val date = AccountMaskUtils.getExpiryDateString(mask)
                if (date != null) {
                    presetSubtitle.visibility = View.VISIBLE
                    presetSubtitle.text = date
                }
            }
            else -> presetTitle.text = mask.displayLabel
        }
    }

    private fun showLoading() {
        binding.progressbarLoad.isVisible = true
        binding.layoutContent.isVisible = false
    }

    override fun showErrorDialog(errorResId: Int) {
        super.showErrorDialog(errorResId)
        binding.progressbarLoad.isVisible = false
        binding.layoutContent.isVisible = true
    }

    override fun onErrorDialogClosed() {
        startActivity(SettingsActivity.createStartIntent(this))
        supportFinishAfterTransition()
    }

    private fun showPaymentList() {
        val paymentUI = PaymentUI.getInstance()
        paymentUI.showPaymentPage(this, EDIT_REQUEST_CODE)
    }

    private fun onPayClicked() {
        if (presetAccount != null) {
            val paymentUI = PaymentUI.getInstance()
            paymentUI.chargePresetAccount(this, PAYMENT_REQUEST_CODE)
        }
    }

    /**
     * Only called from test, creates and returns a new IdlingResource
     */
    @VisibleForTesting
    fun getLoadIdlingResource(): IdlingResource {
        if (loadIdlingResource == null) {
            loadIdlingResource = SimpleIdlingResource("summaryLoadIdlingResource")
        }
        if (loadCompleted) {
            loadIdlingResource!!.setIdleState(loadCompleted)
        }
        return loadIdlingResource!!
    }

    companion object {

        /**
         * Create an Intent to launch this checkout activity
         *
         * @param context the context
         * @param listUrl the URL pointing to the list
         * @return the newly created intent
         */
        fun createStartIntent(context: Context?, listUrl: String?): Intent {
            requireNotNull(context) { "context may not be null" }
            require(!TextUtils.isEmpty(listUrl)) { "listUrl may not be null or empty" }
            val intent = Intent(context, SummaryActivity::class.java)
            intent.putExtra(EXTRA_LISTURL, listUrl)
            return intent
        }
    }
}