/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.page;

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
import com.payoneer.checkout.payment.PaymentServicePresenter;
import com.payoneer.checkout.payment.PaymentServiceViewModel;
import com.payoneer.checkout.payment.RequestData;
import com.payoneer.checkout.ui.dialog.PaymentDialogFragment.PaymentDialogListener;
import com.payoneer.checkout.ui.list.PaymentListListener;
import com.payoneer.checkout.ui.model.AccountCard;
import com.payoneer.checkout.ui.model.PaymentCard;
import com.payoneer.checkout.ui.model.PaymentSession;
import com.payoneer.checkout.ui.model.PresetCard;
import com.payoneer.checkout.ui.session.PaymentSessionListener;
import com.payoneer.checkout.ui.session.PaymentSessionService;
import com.payoneer.checkout.util.PaymentUtils;

import android.content.Context;
import android.text.TextUtils;
import androidx.fragment.app.Fragment;

/**
 * The PaymentListPresenter implementing the presenter part of the MVP
 */
final class PaymentListPresenter extends BasePaymentPresenter
    implements PaymentSessionListener, PaymentServicePresenter, PaymentListListener {

    private final PaymentSessionService sessionService;
    private final PaymentListView listView;
    private final CheckoutConfiguration configuration;

    private PaymentSession session;
    private PaymentService paymentService;
    private RequestData requestData;

    /**
     * Create a new PaymentListPresenter
     *
     * @param view The PaymentListView displaying the payment list
     * @param checkoutConfiguration containing the configuration e.g. listURL
     */
    PaymentListPresenter(PaymentListView view, CheckoutConfiguration checkoutConfiguration) {
        super(checkoutConfiguration, view);
        this.listView = view;
        this.configuration = checkoutConfiguration;

        sessionService = new PaymentSessionService();
        sessionService.setListener(this);
    }

    void onStart() {
        if (paymentService != null && paymentService.isPending()) {
            setState(PROCESS);
            paymentService.resume();
            return;
        }
        setState(STARTED);
        if (session == null) {
            loadPaymentSession();
        } else {
            showPaymentSession();
        }
    }

    void onStop() {
        setState(STOPPED);
        sessionService.onStop();

        if (paymentService != null) {
            paymentService.onStop();
        }
    }

    @Override
    public Context getApplicationContext() {
        return view.getActivity();
    }

    public void onRefresh(boolean hasUserInputData) {
        if (!checkState(STARTED)) {
            return;
        }
        if (!hasUserInputData) {
            loadPaymentSession();
            return;
        }
        PaymentDialogListener listener = new PaymentDialogListener() {
            @Override
            public void onPositiveButtonClicked() {
                loadPaymentSession();
            }

            @Override
            public void onNegativeButtonClicked() {
            }

            @Override
            public void onDismissed() {
            }
        };
        view.showRefreshAccountDialog(listener);
    }

    @Override
    public void setPaymentServiceViewModel(final PaymentServiceViewModel serviceViewModel) {
    }

    @Override
    public void onActionClicked(PaymentCard paymentCard, PaymentInputValues inputValues) {
        if (!checkState(STARTED)) {
            return;
        }
        if (paymentCard instanceof PresetCard) {
            onPresetCardSelected((PresetCard) paymentCard);
            return;
        }
        processPaymentCard(paymentCard, inputValues);
    }

    @Override
    public void onDeleteClicked(PaymentCard paymentCard) {
        if (!checkState(STARTED)) {
            return;
        }
        if (!(paymentCard instanceof AccountCard)) {
            return;
        }
        view.showDeleteAccountDialog(new PaymentDialogListener() {
            @Override
            public void onPositiveButtonClicked() {
                deleteAccountCard((AccountCard) paymentCard);
            }

            @Override
            public void onNegativeButtonClicked() {
            }

            @Override
            public void onDismissed() {
            }
        }, paymentCard.getTitle());
    }

    @Override
    public void onHintClicked(String networkCode, String type) {
        view.showHintDialog(networkCode, type, null);
    }

    @Override
    public void onExpiredIconClicked(String networkCode) {
        view.showExpiredDialog(networkCode);
    }

    @Override
    public void onPaymentSessionSuccess(PaymentSession session) {
        ListResult listResult = session.getListResult();
        Interaction interaction = listResult.getInteraction();

        if (Objects.equals(interaction.getCode(), PROCEED)) {
            handleLoadPaymentSessionProceed(session);
        } else {
            ErrorInfo errorInfo = new ErrorInfo(listResult.getResultInfo(), interaction);
            closeWithErrorCode(new CheckoutResult(errorInfo));
        }
    }

    @Override
    public void onPaymentSessionError(Throwable cause) {
        CheckoutResult result = CheckoutResultHelper.fromThrowable(cause);
        if (result.isNetworkFailure()) {
            handleLoadingNetworkFailure(result);
        } else {
            closeWithErrorCode(result);
        }
    }

    @Override
    public void finalizePayment() {
        view.showProgress(true);
    }

    @Override
    public void showFragment(final Fragment fragment) {

    }

    @Override
    public void onDeleteAccountResult(final CheckoutResult result) {
        setState(STARTED);
        if (result.isNetworkFailure()) {
            handleDeleteAccountNetworkFailure(result);
            return;
        }
        Interaction interaction = result.getInteraction();
        switch (interaction.getCode()) {
            case PROCEED:
            case RELOAD:
                loadPaymentSession();
                break;
            case RETRY:
                showMessageAndPaymentSession(InteractionMessage.fromDeleteFlow(interaction));
                break;
            case TRY_OTHER_ACCOUNT:
            case TRY_OTHER_NETWORK:
                showMessageAndReloadPaymentSession(InteractionMessage.fromDeleteFlow(interaction));
                break;
            default:
                closeWithErrorCode(result);
        }
    }

    @Override
    public void onProcessPaymentResult(final CheckoutResult result) {
        setState(STARTED);
        if (UPDATE.equals(session.getListOperationType())) {
            handleUpdateCheckoutResult(result);
        } else {
            handleProcessPaymentResult(result);
        }
    }

    private void handleUpdateCheckoutResult(final CheckoutResult result) {
        if (result.isProceed()) {
            handleUpdatePaymentProceed(result);
        } else {
            handleUpdatePaymentError(result);
        }
    }

    private void handleUpdatePaymentProceed(final CheckoutResult result) {
        Interaction interaction = result.getInteraction();
        switch (interaction.getReason()) {
            case PENDING:
                showMessageAndResetPaymentSession(createInteractionMessage(interaction, session));
                break;
            case OK:
                loadPaymentSession();
                break;
            default:
                closeWithProceedCode(result);
        }
    }

    private void handleUpdatePaymentError(final CheckoutResult result) {
        if (result.isNetworkFailure()) {
            handleProcessNetworkFailure(result);
            return;
        }
        Interaction interaction = result.getInteraction();
        switch (interaction.getCode()) {
            case RELOAD:
                loadPaymentSession();
                break;
            case TRY_OTHER_ACCOUNT:
            case TRY_OTHER_NETWORK:
            case RETRY:
                showMessageAndReloadPaymentSession(createInteractionMessage(interaction, session));
                break;
            default:
                closeWithErrorCode(result);
        }
    }

    private void handleProcessPaymentResult(final CheckoutResult result) {
        if (result.isProceed()) {
            closeWithProceedCode(result);
        } else {
            handleProcessPaymentError(result);
        }
    }

    private void handleProcessPaymentError(final CheckoutResult result) {
        if (result.isNetworkFailure()) {
            handleProcessNetworkFailure(result);
            return;
        }
        Interaction interaction = result.getInteraction();
        switch (interaction.getCode()) {
            case RELOAD:
                loadPaymentSession();
                break;
            case TRY_OTHER_ACCOUNT:
            case TRY_OTHER_NETWORK:
                showMessageAndReloadPaymentSession(createInteractionMessage(interaction, session));
                break;
            case RETRY:
                showMessageAndPaymentSession(createInteractionMessage(interaction, session));
                break;
            default:
                closeWithErrorCode(result);
        }
    }

    private void handleProcessNetworkFailure(final CheckoutResult result) {
        view.showConnectionErrorDialog(new PaymentDialogListener() {
            @Override
            public void onPositiveButtonClicked() {
                processPayment(requestData);
            }

            @Override
            public void onNegativeButtonClicked() {
                closeWithErrorCode(result);
            }

            @Override
            public void onDismissed() {
                closeWithErrorCode(result);
            }
        });
    }

    private void handleLoadPaymentSessionProceed(PaymentSession session) {
        if (session.isEmpty()) {
            closeWithErrorCode("There are no payment methods available");
            return;
        }
        this.session = session;
        showPaymentSession();
    }

    private void handleLoadingNetworkFailure(final CheckoutResult checkoutResult) {
        view.showConnectionErrorDialog(new PaymentDialogListener() {
            @Override
            public void onPositiveButtonClicked() {
                loadPaymentSession();
            }

            @Override
            public void onNegativeButtonClicked() {
                closeWithErrorCode(checkoutResult);
            }

            @Override
            public void onDismissed() {
                closeWithErrorCode(checkoutResult);
            }
        });
    }

    private void handleDeleteAccountNetworkFailure(final CheckoutResult checkoutResult) {
        view.showConnectionErrorDialog(new PaymentDialogListener() {
            @Override
            public void onPositiveButtonClicked() {
                deleteAccount(requestData);
            }

            @Override
            public void onNegativeButtonClicked() {
                showPaymentSession();
            }

            @Override
            public void onDismissed() {
                showPaymentSession();
            }
        });
    }

    private void processPaymentCard(PaymentCard paymentCard, PaymentInputValues inputValues) {
        try {
            paymentService = loadPaymentService(paymentCard.getNetworkCode(), paymentCard.getPaymentMethod());
            paymentService.setPresenter(this);

            requestData = new RequestData(session.getListOperationType(), paymentCard.getNetworkCode(),
                paymentCard.getPaymentMethod(), paymentCard.getOperationType(),
                paymentCard.getLinks(), inputValues);

            processPayment(requestData);
        } catch (PaymentException e) {
            closeWithErrorCode(CheckoutResultHelper.fromThrowable(e));
        }
    }

    private void deleteAccountCard(AccountCard card) {
        try {
            paymentService = loadPaymentService(card.getNetworkCode(), card.getPaymentMethod());
            paymentService.setPresenter(this);

            requestData = new RequestData(session.getListOperationType(), card.getNetworkCode(),
                card.getPaymentMethod(), card.getOperationType(),
                card.getLinks(), new PaymentInputValues());
            deleteAccount(requestData);
        } catch (PaymentException e) {
            closeWithErrorCode(CheckoutResultHelper.fromThrowable(e));
        }
    }

    private void processPayment(final RequestData requestData) {
        setState(PROCESS);
        paymentService.processPayment(requestData);
    }

    private void deleteAccount(final RequestData requestData) {
        setState(PROCESS);
        paymentService.deleteAccount(requestData);
    }

    private void onPresetCardSelected(PresetCard card) {
        Redirect redirect = card.getPresetAccount().getRedirect();
        List<Parameter> parameters = redirect.getParameters();

        String code = PaymentUtils.getParameterValue(INTERACTION_CODE, parameters);
        String reason = PaymentUtils.getParameterValue(INTERACTION_REASON, parameters);
        if (TextUtils.isEmpty(code) || TextUtils.isEmpty(reason)) {
            closeWithErrorCode("Missing Interaction code and reason inside PresetAccount.redirect");
            return;
        }
        OperationResult result = new OperationResult();
        result.setResultInfo("PresetAccount selected");
        result.setInteraction(new Interaction(code, reason));
        result.setRedirect(redirect);
        closeWithProceedCode(new CheckoutResult(result));
    }

    private void loadPaymentSession() {
        this.session = null;
        listView.clearPaymentList();
        view.showProgress(true);
        sessionService.loadPaymentSession(configuration, view.getActivity());
    }

    private void showMessageAndResetPaymentSession(InteractionMessage message) {
        view.showInteractionDialog(message, null);
        listView.clearPaymentList();
        listView.showPaymentSession(session);
    }

    private void showMessageAndReloadPaymentSession(InteractionMessage message) {
        PaymentDialogListener listener = new PaymentDialogListener() {
            @Override
            public void onPositiveButtonClicked() {
                loadPaymentSession();
            }

            @Override
            public void onNegativeButtonClicked() {
                loadPaymentSession();
            }

            @Override
            public void onDismissed() {
                loadPaymentSession();
            }
        };
        view.showInteractionDialog(message, listener);
    }

    private void showMessageAndPaymentSession(InteractionMessage message) {
        view.showInteractionDialog(message, null);
        listView.showPaymentSession(session);
    }

    private void showPaymentSession() {
        listView.showPaymentSession(session);
    }
}

