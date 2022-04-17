/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.screen.list;

import com.payoneer.checkout.CheckoutResult;
import com.payoneer.checkout.localization.InteractionMessage;
import com.payoneer.checkout.payment.PaymentInputValues;
import com.payoneer.checkout.ui.dialog.PaymentDialogData;
import com.payoneer.checkout.ui.dialog.PaymentDialogFragment.PaymentDialogListener;
import com.payoneer.checkout.ui.model.PaymentCard;
import com.payoneer.checkout.ui.model.PaymentSession;
import com.payoneer.checkout.util.AppContextViewModel;
import com.payoneer.checkout.util.ContentEvent;
import com.payoneer.checkout.util.Resource;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;

/**
 * PaymentListViewModel provides LiveData between the PaymentListActivity and PaymentListPresenter.
 * It operates within the lifecycle of the PaymentListActivity.
 */
final class PaymentListViewModel extends AppContextViewModel {
    private final PaymentListPresenter presenter;
    MutableLiveData<Resource<PaymentSession>> showPaymentSession;
    MutableLiveData<ContentEvent<CheckoutResult>> closeWithCheckoutResult;
    MutableLiveData<ContentEvent<PaymentDialogData>> showPaymentDialog;
    MutableLiveData<ContentEvent<Boolean>> onProcessPayment;
    MutableLiveData<ContentEvent<Boolean>> showPaymentListProgress;
    MutableLiveData<ContentEvent<Boolean>> showTransactionProgress;

    PaymentListViewModel(final Context applicationContext, final PaymentListPresenter presenter) {
        super(applicationContext);
        this.presenter = presenter;

        this.onProcessPayment = new MutableLiveData<>();
        this.showPaymentSession = new MutableLiveData<>();
        this.closeWithCheckoutResult = new MutableLiveData<>();
        this.showPaymentDialog = new MutableLiveData<>();
        this.showPaymentListProgress = new MutableLiveData<>();
        this.showTransactionProgress = new MutableLiveData<>();

        this.presenter.setListViewModel(this);
    }

    void loadPaymentSession() {
        presenter.loadPaymentSession();
    }

    void deletePaymentCard(final PaymentCard paymentCard) {
        presenter.deletePaymentCard(paymentCard);
    }

    void processPaymentCard(final PaymentCard paymentCard, final PaymentInputValues inputValues) {
        presenter.processPaymentCard(paymentCard, inputValues);
    }

    void closeWithCheckoutResult(final CheckoutResult checkoutResult) {
        closeWithCheckoutResult.setValue(new ContentEvent<>(checkoutResult));
    }

    void showPaymentSession(final int status, final PaymentSession paymentSession, final String message) {
        switch (status) {
            case Resource.SUCCESS:
                showPaymentSession.setValue(Resource.success(paymentSession));
                break;
            case Resource.LOADING:
                showPaymentSession.setValue(Resource.loading());
                break;
            case Resource.ERROR:
                showPaymentSession.setValue(Resource.error(message));
        }
    }

    void showConnectionErrorDialog(final PaymentDialogListener listener) {
        PaymentDialogData data = PaymentDialogData.connectionErrorDialog(listener);
        showPaymentDialog.setValue(new ContentEvent<>(data));
    }

    void showInteractionDialog(final PaymentDialogListener listener, final InteractionMessage interactionMessage) {
        PaymentDialogData data = PaymentDialogData.interactionDialog(listener, interactionMessage);
        showPaymentDialog.setValue(new ContentEvent<>(data));
    }

    void onProcessPayment(final Boolean transaction) {
        onProcessPayment.setValue(new ContentEvent<>(transaction));
    }

    void showPaymentListProgress(final Boolean visible) {
        showPaymentListProgress.setValue(new ContentEvent<>(visible));
    }

    void showTransactionProgress(final Boolean visible) {
        showTransactionProgress.setValue(new ContentEvent<>(visible));
    }
}
