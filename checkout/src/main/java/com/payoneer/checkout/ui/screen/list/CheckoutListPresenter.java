/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.screen.list;

import static com.payoneer.checkout.model.InteractionCode.PROCEED;

import java.util.Objects;

import com.payoneer.checkout.CheckoutConfiguration;
import com.payoneer.checkout.CheckoutResult;
import com.payoneer.checkout.CheckoutResultHelper;
import com.payoneer.checkout.model.ErrorInfo;
import com.payoneer.checkout.model.Interaction;
import com.payoneer.checkout.model.ListResult;
import com.payoneer.checkout.payment.PaymentInputValues;
import com.payoneer.checkout.payment.PaymentService;
import com.payoneer.checkout.payment.RequestData;
import com.payoneer.checkout.ui.dialog.PaymentDialogFragment.PaymentDialogListener;
import com.payoneer.checkout.ui.model.PaymentCard;
import com.payoneer.checkout.ui.model.PaymentSession;
import com.payoneer.checkout.ui.session.PaymentSessionListener;
import com.payoneer.checkout.ui.session.PaymentSessionService;
import com.payoneer.checkout.util.Resource;

/**
 * The CheckoutListPresenter
 */
final class CheckoutListPresenter implements PaymentSessionListener {

    private final PaymentSessionService sessionService;
    private final CheckoutConfiguration configuration;

    private CheckoutListViewModel viewModel;
    private PaymentSession paymentSession;
    private PaymentService paymentService;
    private RequestData requestData;

    /**
     * Create a new CheckoutListPresenter
     *
     * @param checkoutConfiguration containing the configuration e.g. listURL
     */
    CheckoutListPresenter(CheckoutConfiguration checkoutConfiguration) {
        this.configuration = checkoutConfiguration;
        sessionService = new PaymentSessionService();
        sessionService.setListener(this);
    }

    void setViewModel(final CheckoutListViewModel viewModel) {
        this.viewModel = viewModel;
    }

    void loadPaymentSession() {
        this.paymentSession = null;
        viewModel.clearPaymentSession();
        viewModel.showPaymentSession(Resource.LOADING, null, null);
        sessionService.loadPaymentSession(configuration, viewModel.getApplicationContext());
    }

    void deletePaymentCard(final PaymentCard paymentCard) {
    }

    void processPaymentCard(final PaymentCard paymentCard, final PaymentInputValues inputValues) {
        
    }

    @Override
    public void onPaymentSessionSuccess(PaymentSession session) {
        ListResult listResult = session.getListResult();
        Interaction interaction = listResult.getInteraction();

        if (Objects.equals(interaction.getCode(), PROCEED)) {
            handleLoadPaymentSessionProceed(session);
        } else {
            ErrorInfo errorInfo = new ErrorInfo(listResult.getResultInfo(), interaction);
            viewModel.closeWithCheckoutResult(new CheckoutResult(errorInfo));
        }
    }

    @Override
    public void onPaymentSessionError(Throwable cause) {
        CheckoutResult result = CheckoutResultHelper.fromThrowable(cause);
        if (result.isNetworkFailure()) {
            handleLoadPaymentSessionNetworkFailure(result);
        } else {
            viewModel.closeWithCheckoutResult(result);
        }
    }

    private void closeWithErrorMessage(String message) {
        CheckoutResult result = CheckoutResultHelper.fromErrorMessage(message);
        viewModel.closeWithCheckoutResult(result);
    }

    private void handleLoadPaymentSessionNetworkFailure(final CheckoutResult checkoutResult) {
        viewModel.showConnectionErrorDialog(new PaymentDialogListener() {
            @Override
            public void onPositiveButtonClicked() {
                loadPaymentSession();
            }

            @Override
            public void onNegativeButtonClicked() {
                viewModel.closeWithCheckoutResult(checkoutResult);
            }

            @Override
            public void onDismissed() {
                viewModel.closeWithCheckoutResult(checkoutResult);
            }
        });
    }

    private void handleLoadPaymentSessionProceed(PaymentSession paymentSession) {
        if (paymentSession.isEmpty()) {
            closeWithErrorMessage("There are no payment methods available");
            return;
        }
        this.paymentSession = paymentSession;
        viewModel.showPaymentSession(Resource.SUCCESS, paymentSession, null);
    }
}

