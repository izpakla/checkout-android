/*
 *
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 *
 */

package com.payoneer.checkout.exampleshop.summary

import android.app.Activity
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.payoneer.checkout.core.PaymentException
import com.payoneer.checkout.exampleshop.util.ContentEvent
import com.payoneer.checkout.exampleshop.util.CoroutineResult
import com.payoneer.checkout.exampleshop.util.Event
import com.payoneer.checkout.exampleshop.util.Resource
import com.payoneer.checkout.model.InteractionCode
import com.payoneer.checkout.model.ListResult
import com.payoneer.checkout.model.PresetAccount
import com.payoneer.checkout.network.ListConnection
import com.payoneer.checkout.ui.PaymentActivityResult
import com.payoneer.checkout.ui.PaymentResult
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class ShopSummaryViewModel @Inject constructor(@ApplicationContext val context: Context) :
    ViewModel() {

    private val _showPaymentConfirmation = MutableLiveData<Event>()
    val showPaymentConfirmation: LiveData<Event> get() = _showPaymentConfirmation

    private val _showPaymentList = MutableLiveData<Event>()
    val showPaymentList: LiveData<Event> get() = _showPaymentList

    private val _stopPaymentWithErrorMessage = MutableLiveData<Event>()
    val stopPaymentWithErrorMessage: LiveData<Event> get() = _stopPaymentWithErrorMessage

    private val _presetAccount = MutableLiveData<Resource<PresetAccount>>()
    val presetAccount: LiveData<Resource<PresetAccount>> get() = _presetAccount

    private val _loadPaymentDetails = MutableLiveData<ContentEvent<Boolean>>()
    val loadPaymentDetails: LiveData<ContentEvent<Boolean>> get() = _loadPaymentDetails

    /**
     * Handle the received payment activity result.
     *
     * @param activityResult the result received from the Checkout SDK
     */
    fun handlePaymentActivityResult(activityResult: PaymentActivityResult) {
        when (activityResult.requestCode) {
            SummaryActivity.PAYMENT_REQUEST_CODE -> handlePaymentResult(activityResult)
            SummaryActivity.EDIT_REQUEST_CODE -> handleEditResult(activityResult)
        }
    }

    @Suppress("KotlinConstantConditions")
    fun loadPaymentDetails(listUrl: String) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                _presetAccount.postValue(Resource.loading())
                val result: CoroutineResult<ListResult>? =
                    suspendCoroutine { continuation ->
                        try {
                            val listResult = ListConnection(context).getListResult(listUrl)
                            continuation.resume(CoroutineResult(data = listResult))
                        } catch (e: PaymentException) {
                            continuation.resume(CoroutineResult(error = e))
                        }
                    }
                if (result != null) {
                    if (result.data != null) {
                        _presetAccount.postValue(Resource.success(data = result.data.presetAccount))
                    } else {
                        _presetAccount.postValue(Resource.error("Something went wrong. Please try again"))
                    }
                } else {
                    _presetAccount.postValue(
                        Resource.error(
                            message = result?.error?.localizedMessage
                                ?: "Something went wrong. Please try again"
                        )
                    )
                }
            }
        }
    }

    private fun handleEditResult(result: PaymentActivityResult) {
        when (result.resultCode) {
            PaymentActivityResult.RESULT_CODE_ERROR -> handlePaymentResultError(result.paymentResult)
            Activity.RESULT_CANCELED, PaymentActivityResult.RESULT_CODE_PROCEED -> _loadPaymentDetails.value =
                ContentEvent(true)
        }
    }

    private fun handlePaymentResult(activityResult: PaymentActivityResult) {
        val paymentResult = activityResult.paymentResult
        when (activityResult.resultCode) {
            PaymentActivityResult.RESULT_CODE_PROCEED -> handlePaymentResultProceed(paymentResult)
            PaymentActivityResult.RESULT_CODE_ERROR -> handlePaymentResultError(paymentResult)
        }
    }

    private fun handlePaymentResultProceed(result: PaymentResult) {
        val interaction = result.interaction
        if (interaction != null) {
            _showPaymentConfirmation.value = Event()
        }
    }

    private fun handlePaymentResultError(result: PaymentResult) {
        val interaction = result.interaction
        when (interaction.code) {
            InteractionCode.ABORT -> if (!result.isNetworkFailure) {
                _stopPaymentWithErrorMessage.value = Event()
            }
            // VERIFY means that a charge request has been made but the status of the payment could
            // not be verified by the Checkout SDK, i.e. because of a network error
            InteractionCode.VERIFY ->
                _stopPaymentWithErrorMessage.value = Event()
            InteractionCode.TRY_OTHER_ACCOUNT, InteractionCode.TRY_OTHER_NETWORK, InteractionCode.RELOAD, InteractionCode.RETRY -> _showPaymentList.value =
                Event()
        }
    }
}