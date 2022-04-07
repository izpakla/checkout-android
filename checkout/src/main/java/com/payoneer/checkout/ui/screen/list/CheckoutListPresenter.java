/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.screen.list;

import static com.payoneer.checkout.CheckoutActivityResult.RESULT_CODE_ERROR;
import static com.payoneer.checkout.CheckoutActivityResult.RESULT_CODE_PROCEED;
import static com.payoneer.checkout.model.InteractionCode.PROCEED;
import static com.payoneer.checkout.model.InteractionCode.RELOAD;
import static com.payoneer.checkout.model.InteractionCode.RETRY;
import static com.payoneer.checkout.model.InteractionCode.TRY_OTHER_ACCOUNT;
import static com.payoneer.checkout.model.InteractionCode.TRY_OTHER_NETWORK;
import static com.payoneer.checkout.model.InteractionReason.OK;
import static com.payoneer.checkout.model.InteractionReason.PENDING;
import static com.payoneer.checkout.model.NetworkOperationType.UPDATE;
import static com.payoneer.checkout.redirect.RedirectService.INTERACTION_CODE;
import static com.payoneer.checkout.redirect.RedirectService.INTERACTION_REASON;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.payoneer.checkout.CheckoutConfiguration;
import com.payoneer.checkout.CheckoutResult;
import com.payoneer.checkout.CheckoutResultHelper;
import com.payoneer.checkout.core.PaymentException;
import com.payoneer.checkout.localization.InteractionMessage;
import com.payoneer.checkout.model.ErrorInfo;
import com.payoneer.checkout.model.Interaction;
import com.payoneer.checkout.model.ListResult;
import com.payoneer.checkout.model.OperationResult;
import com.payoneer.checkout.model.Parameter;
import com.payoneer.checkout.model.Redirect;
import com.payoneer.checkout.payment.PaymentInputValues;
import com.payoneer.checkout.payment.PaymentService;
import com.payoneer.checkout.payment.PaymentServiceController;
import com.payoneer.checkout.payment.PaymentServiceLookup;
import com.payoneer.checkout.payment.RequestData;
import com.payoneer.checkout.ui.dialog.PaymentDialogFragment.PaymentDialogListener;
import com.payoneer.checkout.ui.model.AccountCard;
import com.payoneer.checkout.ui.model.PaymentCard;
import com.payoneer.checkout.ui.model.PaymentSession;
import com.payoneer.checkout.ui.model.PresetCard;
import com.payoneer.checkout.ui.session.PaymentSessionListener;
import com.payoneer.checkout.ui.session.PaymentSessionService;
import com.payoneer.checkout.ui.widget.FormWidget;
import com.payoneer.checkout.util.PaymentUtils;

import android.content.Context;
import android.text.TextUtils;

/**
 * The CheckoutListPresenter
 */
final class CheckoutListPresenter implements PaymentSessionListener {

    private final PaymentSessionService sessionService;
    private final CheckoutConfiguration configuration;

    private CheckoutListViewModel viewModel;
    private PaymentSession session;
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

    public void loadPaymentSession() {
        sessionService.loadPaymentSession(configuration, viewModel.getApplicationContext());
    }

    @Override
    public void onPaymentSessionSuccess(PaymentSession session) {
        ListResult listResult = session.getListResult();
        Interaction interaction = listResult.getInteraction();

        if (Objects.equals(interaction.getCode(), PROCEED)) {
            handleLoadPaymentSessionProceed(session);
        } else {
            ErrorInfo errorInfo = new ErrorInfo(listResult.getResultInfo(), interaction);
            //closeWithErrorCode(new CheckoutResult(errorInfo));
        }
    }

    @Override
    public void onPaymentSessionError(Throwable cause) {
        CheckoutResult result = CheckoutResultHelper.fromThrowable(cause);
        if (result.isNetworkFailure()) {
            //handleLoadingNetworkFailure(result);
        } else {
            //closeWithErrorCode(result);
        }
    }

    private void handleLoadPaymentSessionProceed(PaymentSession session) {
        if (session.isEmpty()) {
            //closeWithErrorCode("There are no payment methods available");
            return;
        }
        this.session = session;
        viewModel.setPaymentSession(session);
    }
}

