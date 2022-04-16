/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.screen.payment;

import com.payoneer.checkout.CheckoutResult;
import com.payoneer.checkout.localization.InteractionMessage;
import com.payoneer.checkout.ui.dialog.PaymentDialogData;
import com.payoneer.checkout.ui.dialog.PaymentDialogFragment.PaymentDialogListener;
import com.payoneer.checkout.util.AppContextViewModel;
import com.payoneer.checkout.util.ContentEvent;
import com.payoneer.checkout.util.Event;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;

/**
 * ProcessPaymentViewModel provides LiveData for the ProcessPaymentActivity and ProcessPaymentPresenter.
 * It operates within the lifecycle of the ProcessPaymentActivity.
 */
final class ProcessPaymentViewModel extends AppContextViewModel {
    MutableLiveData<ContentEvent<Boolean>> showProgress;
    MutableLiveData<ContentEvent<CheckoutResult>> closeWithCheckoutResult;
    MutableLiveData<ContentEvent<PaymentDialogData>> showPaymentDialog;
    MutableLiveData<Event> showProcessPayment;

    ProcessPaymentViewModel(final Context applicationContext, final ProcessPaymentPresenter presenter) {
        super(applicationContext);
        this.showProgress = new MutableLiveData<>();
        this.closeWithCheckoutResult = new MutableLiveData<>();
        this.showPaymentDialog = new MutableLiveData<>();
        this.showProcessPayment = new MutableLiveData<>();

        presenter.setPaymentViewModel(this);
    }

    void closeWithCheckoutResult(final CheckoutResult checkoutResult) {
        closeWithCheckoutResult.setValue(new ContentEvent<>(checkoutResult));
    }

    void showProcessPayment() {
        showProcessPayment.setValue(new Event());
    }

    void showProgress(final Boolean visible) {
        showProgress.setValue(new ContentEvent<>(visible));
    }

    void showConnectionErrorDialog(final PaymentDialogListener listener) {
        PaymentDialogData data = PaymentDialogData.connectionErrorDialog(listener);
        showPaymentDialog.setValue(new ContentEvent<>(data));
    }

    void showInteractionDialog(final PaymentDialogListener listener, final InteractionMessage interactionMessage) {
        PaymentDialogData data = PaymentDialogData.interactionDialog(listener, interactionMessage);
        showPaymentDialog.setValue(new ContentEvent<>(data));
    }


}
