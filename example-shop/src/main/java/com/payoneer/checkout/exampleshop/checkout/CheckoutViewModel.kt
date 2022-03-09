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
import com.payoneer.checkout.CheckoutActivityResult
import com.payoneer.checkout.CheckoutResult
import com.payoneer.checkout.exampleshop.util.Event
import com.payoneer.checkout.model.InteractionCode
import com.payoneer.checkout.model.RedirectType
import com.payoneer.checkout.util.PaymentUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor() : ViewModel() {

    private val _showPaymentSummary = MutableLiveData<Event>()
    val showPaymentSummary: LiveData<Event> get() = _showPaymentSummary

    private val _showPaymentConfirmation = MutableLiveData<Event>()
    val showPaymentConfirmation: LiveData<Event> get() = _showPaymentConfirmation

    private val _stopPaymentWithErrorMessage = MutableLiveData<Event>()
    val stopPaymentWithErrorMessage: LiveData<Event> get() = _stopPaymentWithErrorMessage

    /**
     * Handle the CheckoutActivityResult received from the Checkout SDK.
     *
     * @param activityResult containing the checkout result
     */
    fun handleCheckoutActivityResult(activityResult: CheckoutActivityResult) {
        val checkoutResult = activityResult.checkoutResult
        when (activityResult.resultCode) {
            CheckoutActivityResult.RESULT_CODE_PROCEED -> handleCheckoutResultProceed(checkoutResult)
            CheckoutActivityResult.RESULT_CODE_ERROR -> handleCheckoutResultError(checkoutResult)
        }
    }

    private fun handleCheckoutResultProceed(result: CheckoutResult) {
        if (result.interaction == null) return
        if (PaymentUtils.containsRedirectType(result.operationResult, RedirectType.SUMMARY)) {
            _showPaymentSummary.value = Event()
            return
        }
        _showPaymentConfirmation.value = Event()
    }

    private fun handleCheckoutResultError(result: CheckoutResult) {
        val interaction = result.interaction
        when (interaction.code) {
            InteractionCode.ABORT -> {
                if (!result.isNetworkFailure) {
                    _stopPaymentWithErrorMessage.value = Event()
                }
            }
            // VERIFY means that a charge request has been made but the status of the payment could
            // not be verified by the Checkout SDK, i.e. because of a network error
            InteractionCode.VERIFY -> {
                _stopPaymentWithErrorMessage.value = Event()
            }
        }
    }
}