/*
 *
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 *
 */

package com.payoneer.checkout.exampleshop.checkout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.payoneer.checkout.exampleshop.Event
import com.payoneer.checkout.exampleshop.updateValue
import com.payoneer.checkout.model.InteractionCode
import com.payoneer.checkout.model.RedirectType
import com.payoneer.checkout.ui.PaymentActivityResult
import com.payoneer.checkout.ui.PaymentResult
import com.payoneer.checkout.util.PaymentUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ShopCheckoutViewModel @Inject constructor() : ViewModel() {

    private val _showPaymentSummary = MutableLiveData<Event>()
    val showPaymentSummary: LiveData<Event> = _showPaymentSummary

    private val _showPaymentConfirmation = MutableLiveData<Event>()
    val showPaymentConfirmation: LiveData<Event> = _showPaymentConfirmation

    private val _stopPaymentWithErrorMessage = MutableLiveData<Event>()
    val stopPaymentWithErrorMessage: LiveData<Event> = _stopPaymentWithErrorMessage

    /**
     * Handle the PaymentActivityResult received from the Checkout SDK.
     *
     * @param activityResult containing the payment result
     */
    fun handlePaymentActivityResult(activityResult: PaymentActivityResult) {
        val paymentResult = activityResult.paymentResult
        when (activityResult.resultCode) {
            PaymentActivityResult.RESULT_CODE_PROCEED -> handlePaymentResultProceed(paymentResult)
            PaymentActivityResult.RESULT_CODE_ERROR -> handlePaymentResultError(paymentResult)
        }
    }

    private fun handlePaymentResultProceed(result: PaymentResult) {
        if (result.interaction == null) return
        if (PaymentUtils.containsRedirectType(result.operationResult, RedirectType.SUMMARY)) {
            _showPaymentSummary.updateValue()
            return
        }
        _showPaymentSummary.updateValue()
    }

    private fun handlePaymentResultError(result: PaymentResult) {
        val interaction = result.interaction
        when (interaction.code) {
            InteractionCode.ABORT -> {
                if (!result.isNetworkFailure) {
                    _stopPaymentWithErrorMessage.updateValue()
                }
            }
            // VERIFY means that a charge request has been made but the status of the payment could
            // not be verified by the Checkout SDK, i.e. because of a network error
            InteractionCode.VERIFY -> {
                _stopPaymentWithErrorMessage.updateValue()
            }
        }
    }
}