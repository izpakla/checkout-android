/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.page;

import static com.payoneer.checkout.CheckoutActivityResult.RESULT_CODE_ERROR;
import static com.payoneer.checkout.CheckoutActivityResult.RESULT_CODE_PROCEED;
import static com.payoneer.checkout.model.InteractionCode.PROCEED;
import static com.payoneer.checkout.model.InteractionCode.RELOAD;
import static com.payoneer.checkout.model.InteractionCode.RETRY;
import static com.payoneer.checkout.model.InteractionCode.TRY_OTHER_ACCOUNT;
import static com.payoneer.checkout.model.InteractionCode.TRY_OTHER_NETWORK;
import static com.payoneer.checkout.model.InteractionReason.OK;
import static com.payoneer.checkout.model.InteractionReason.PENDING;
import static com.payoneer.checkout.model.NetworkOperationType.CHARGE;
import static com.payoneer.checkout.model.NetworkOperationType.UPDATE;
import static com.payoneer.checkout.redirect.RedirectService.INTERACTION_CODE;
import static com.payoneer.checkout.redirect.RedirectService.INTERACTION_REASON;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.payoneer.checkout.CheckoutActivityResult;
import com.payoneer.checkout.CheckoutConfiguration;
import com.payoneer.checkout.CheckoutResult;
import com.payoneer.checkout.CheckoutResultHelper;
import com.payoneer.checkout.core.PaymentException;
import com.payoneer.checkout.core.PaymentLinkType;
import com.payoneer.checkout.localization.InteractionMessage;
import com.payoneer.checkout.model.ErrorInfo;
import com.payoneer.checkout.model.Interaction;
import com.payoneer.checkout.model.ListResult;
import com.payoneer.checkout.model.OperationResult;
import com.payoneer.checkout.model.Parameter;
import com.payoneer.checkout.model.Redirect;
import com.payoneer.checkout.operation.DeleteAccount;
import com.payoneer.checkout.payment.PaymentInputValues;
import com.payoneer.checkout.payment.PaymentRequest;
import com.payoneer.checkout.payment.PaymentService;
import com.payoneer.checkout.payment.PaymentServiceController;
import com.payoneer.checkout.redirect.RedirectRequest;
import com.payoneer.checkout.redirect.RedirectService;
import com.payoneer.checkout.ui.dialog.PaymentDialogFragment.PaymentDialogListener;
import com.payoneer.checkout.ui.list.PaymentListListener;
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
 * The PaymentListPresenter implementing the presenter part of the MVP
 */
final class PaymentListPresenter extends BasePaymentPresenter
    implements PaymentSessionListener, PaymentServiceController, PaymentListListener {

    private final PaymentSessionService sessionService;
    private final PaymentListView listView;
    private final CheckoutConfiguration configuration;

    private PaymentSession session;
    private PaymentService paymentService;
    private PaymentRequest paymentRequest;
    private DeleteAccount deleteAccount;

    /**
     * Create a new PaymentListPresenter
     *
     * @param view The PaymentListView displaying the payment list
     */
    PaymentListPresenter(PaymentListView view, CheckoutConfiguration checkoutConfiguration) {
        super(checkoutConfiguration, view);
        this.listView = view;
        this.configuration = checkoutConfiguration;

        sessionService = new PaymentSessionService();
        sessionService.setListener(this);
    }

    void onStart() {
        setState(STARTED);

        if (paymentService != null && paymentService.isProcessing()) {
            paymentService.resumeProcessing();
        }
        else if (session == null) {
            loadPaymentSession();
        }
        else {
            showPaymentSession();
        }
    }

    void onStop() {
        setState(STOPPED);
        sessionService.stop();

        if (paymentService != null) {
            paymentService.stop();
        }
    }

    @Override
    public Context getContext() {
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
    public void onActionClicked(PaymentCard paymentCard, Map<String, FormWidget> widgets) {
        if (!checkState(STARTED)) {
            return;
        }
        if (paymentCard instanceof PresetCard) {
            onPresetCardSelected((PresetCard) paymentCard);
            return;
        }
        processPaymentCard(paymentCard, widgets);
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
    public void showProgress(boolean visible) {
        view.showProgress(visible);
    }

    @Override
    public void onDeleteAccountResult(int resultCode, CheckoutResult result) {
        if (result.isNetworkFailure()) {
            handleDeleteNetworkFailure(result);
            return;
        }
        Interaction interaction = result.getInteraction();
        setState(STARTED);

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
    public void onProcessPaymentResult(int resultCode, CheckoutResult result) {
        setState(STARTED);
        if (UPDATE.equals(session.getListOperationType())) {
            handleUpdateCheckoutResult(resultCode, result);
        } else {
            handleProcessCheckoutResult(resultCode, result);
        }
    }

    private void handleUpdateCheckoutResult(int resultCode, CheckoutResult result) {
        switch (resultCode) {
            case RESULT_CODE_PROCEED:
                handleUpdatePaymentProceed(result);
                break;
            case RESULT_CODE_ERROR:
                handleUpdatePaymentError(result);
                break;
            default:
                showPaymentSession();
        }
    }

    private void handleUpdatePaymentProceed(CheckoutResult result) {
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

    private void handleUpdatePaymentError(CheckoutResult result) {
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

    private void handleProcessCheckoutResult(int resultCode, CheckoutResult result) {
        switch (resultCode) {
            case RESULT_CODE_PROCEED:
                closeWithProceedCode(result);
                break;
            case RESULT_CODE_ERROR:
                handleProcessCheckoutError(result);
                break;
            default:
                showPaymentSession();
        }
    }

    private void handleProcessCheckoutError(CheckoutResult result) {
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
                processPaymentRequest(paymentRequest);
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

    private void handleLoadingNetworkFailure(final CheckoutResult result) {
        view.showConnectionErrorDialog(new PaymentDialogListener() {
            @Override
            public void onPositiveButtonClicked() {
                loadPaymentSession();
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

    private void handleDeleteNetworkFailure(final CheckoutResult result) {
        view.showConnectionErrorDialog(new PaymentDialogListener() {
            @Override
            public void onPositiveButtonClicked() {
                deleteAccount(deleteAccount);
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

    private void processPaymentCard(PaymentCard paymentCard, Map<String, FormWidget> widgets) {
        try {
            paymentRequest = createPaymentRequest(paymentCard, widgets);
            paymentService = loadNetworkService(paymentCard.getNetworkCode(), paymentCard.getPaymentMethod());
            paymentService.setController(this);
            processPaymentRequest(paymentRequest);
        } catch (PaymentException e) {
            closeWithErrorCode(CheckoutResultHelper.fromThrowable(e));
        }
    }

    private void deleteAccountCard(AccountCard card) {
        try {
            paymentService = loadNetworkService(card.getNetworkCode(), card.getPaymentMethod());
            paymentService.setController(this);

            URL url = card.getLink(PaymentLinkType.SELF);
            deleteAccount = new DeleteAccount(url, session.getListOperationType());
            deleteAccount(deleteAccount);
        } catch (PaymentException e) {
            closeWithErrorCode(CheckoutResultHelper.fromThrowable(e));
        }
    }

    private void processPaymentRequest(final PaymentRequest paymentRequest) {
        setState(PROCESS);
        paymentService.processPayment(paymentRequest, view.getActivity());
    }

    private void deleteAccount(DeleteAccount account) {
        setState(PROCESS);
        paymentService.deleteAccount(account, view.getActivity());
    }

    private PaymentRequest createPaymentRequest(final PaymentCard card, final Map<String, FormWidget> widgets) {
        PaymentInputValues inputValues = new PaymentInputValues();
        for (FormWidget widget : widgets.values()) {
            widget.putValue(inputValues);
        }
        return new PaymentRequest(card.getNetworkCode(), card.getPaymentMethod(),
            card.getOperationType(), card.getLinks(), inputValues);
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

