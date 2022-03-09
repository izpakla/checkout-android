/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.exampleshop.summary;

import static android.app.Activity.RESULT_CANCELED;
import static com.payoneer.checkout.ui.PaymentActivityResult.RESULT_CODE_ERROR;
import static com.payoneer.checkout.ui.PaymentActivityResult.RESULT_CODE_PROCEED;

import java.util.concurrent.Callable;

import com.payoneer.checkout.core.PaymentException;
import com.payoneer.checkout.exampleshop.shared.ShopException;
import com.payoneer.checkout.model.Interaction;
import com.payoneer.checkout.model.InteractionCode;
import com.payoneer.checkout.model.ListResult;
import com.payoneer.checkout.model.PresetAccount;
import com.payoneer.checkout.network.ListConnection;
import com.payoneer.checkout.ui.PaymentActivityResult;
import com.payoneer.checkout.ui.PaymentResult;

import android.content.Context;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * SummaryPresenter responsible for communicating with the
 * Checkout SDK and informing the SummaryView to show content to the user.
 */
final class SummaryPresenter {

    private final SummaryView view;
    private Disposable disposable;

    /**
     * Construct a new SummaryPresenter
     *
     * @param view the view
     */
    SummaryPresenter(SummaryView view) {
        this.view = view;
    }

    /**
     * Notify the presenter that it should be stopped.
     * Dispose any disposable if available.
     */
    void onStop() {
        if (disposable != null) {
            disposable.dispose();
            disposable = null;
        }
    }

    /**
     * Handle the received payment activity result.
     *
     * @param activityResult the result received from the Checkout SDK
     */
    void handlePaymentActivityResult(PaymentActivityResult activityResult) {
        switch (activityResult.getRequestCode()) {
            case SummaryActivity.PAYMENT_REQUEST_CODE:
                handlePaymentResult(activityResult);
                break;
            case SummaryActivity.EDIT_REQUEST_CODE:
                handleEditResult(activityResult);
                break;
        }
    }

    void loadPaymentDetails(String listUrl) {
        if (isLoadSessionActive()) {
            return;
        }
        view.showLoading(true);
        final Context context = view.getContext();
        final Single<ListResult> single = Single.fromCallable(new Callable<ListResult>() {
            @Override
            public ListResult call() throws ShopException {
                return asyncLoadPaymentSession(context, listUrl);
            }
        });

        this.disposable = single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new DisposableSingleObserver<ListResult>() {
                @Override
                public void onStart() {
                }

                @Override
                public void onSuccess(ListResult value) {
                    handleLoadPaymentSessionSuccess(value);
                }

                @Override
                public void onError(Throwable cause) {
                    handleLoadPaymentSessionError(cause);
                }
            });
    }

    private void handleEditResult(PaymentActivityResult result) {
        switch (result.getResultCode()) {
            case RESULT_CODE_ERROR:
                handlePaymentResultError(result.getPaymentResult());
                break;
            case RESULT_CANCELED:
                // This resultCode is returned when the user closed the payment page and there is no payment result available
            case RESULT_CODE_PROCEED:
                loadPaymentDetails(view.getListUrl());
                break;
        }
    }

    private void handlePaymentResult(PaymentActivityResult activityResult) {
        PaymentResult paymentResult = activityResult.getPaymentResult();
        switch (activityResult.getResultCode()) {
            case RESULT_CODE_PROCEED:
                handlePaymentResultProceed(paymentResult);
                break;
            case RESULT_CODE_ERROR:
                handlePaymentResultError(paymentResult);
                break;
        }
    }

    private void handlePaymentResultProceed(PaymentResult result) {
        Interaction interaction = result.getInteraction();
        if (interaction != null) {
            view.showPaymentConfirmation();
        }
    }

    private void handlePaymentResultError(PaymentResult result) {
        Interaction interaction = result.getInteraction();
        switch (interaction.getCode()) {
            case InteractionCode.ABORT:
                if (!result.isNetworkFailure()) {
                    view.stopPaymentWithErrorMessage();
                }
                break;
            case InteractionCode.VERIFY:
                // VERIFY means that a charge request has been made but the status of the payment could
                // not be verified by the Checkout SDK, i.e. because of a network error
                view.stopPaymentWithErrorMessage();
                break;
            case InteractionCode.TRY_OTHER_ACCOUNT:
            case InteractionCode.TRY_OTHER_NETWORK:
            case InteractionCode.RELOAD:
            case InteractionCode.RETRY:
                view.showPaymentList();
                break;
        }
    }

    private boolean isLoadSessionActive() {
        return disposable != null && !disposable.isDisposed();
    }

    private void handleLoadPaymentSessionSuccess(ListResult result) {
        this.disposable = null;
        PresetAccount account = result.getPresetAccount();
        if (account == null) {
            view.close();
            return;
        }
        view.showPaymentDetails(account);
    }

    private void handleLoadPaymentSessionError(Throwable error) {
        this.disposable = null;
        view.stopPaymentWithErrorMessage();
    }

    private ListResult asyncLoadPaymentSession(Context context, String listUrl) throws ShopException {
        ListConnection conn = new ListConnection();
        conn.initialize(context);

        try {
            return conn.getListResult(listUrl);
        } catch (PaymentException e) {
            throw new ShopException("Error loading list result", e);
        }
    }
}
