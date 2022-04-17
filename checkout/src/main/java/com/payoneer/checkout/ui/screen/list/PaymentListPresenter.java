/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.screen.list;

import static com.payoneer.checkout.model.InteractionCode.PROCEED;
import static com.payoneer.checkout.model.InteractionCode.RELOAD;
import static com.payoneer.checkout.model.InteractionCode.RETRY;
import static com.payoneer.checkout.model.InteractionCode.TRY_OTHER_ACCOUNT;
import static com.payoneer.checkout.model.InteractionCode.TRY_OTHER_NETWORK;
import static com.payoneer.checkout.model.InteractionReason.OK;
import static com.payoneer.checkout.model.InteractionReason.PENDING;
import static com.payoneer.checkout.model.NetworkOperationType.CHARGE;
import static com.payoneer.checkout.model.NetworkOperationType.UPDATE;

import java.util.Objects;

import com.payoneer.checkout.CheckoutConfiguration;
import com.payoneer.checkout.CheckoutResult;
import com.payoneer.checkout.CheckoutResultHelper;
import com.payoneer.checkout.core.PaymentException;
import com.payoneer.checkout.localization.InteractionMessage;
import com.payoneer.checkout.model.ErrorInfo;
import com.payoneer.checkout.model.Interaction;
import com.payoneer.checkout.model.ListResult;
import com.payoneer.checkout.payment.PaymentInputValues;
import com.payoneer.checkout.payment.PaymentService;
import com.payoneer.checkout.payment.PaymentServiceLookup;
import com.payoneer.checkout.payment.PaymentServicePresenter;
import com.payoneer.checkout.payment.PaymentServiceViewModel;
import com.payoneer.checkout.payment.RequestData;
import com.payoneer.checkout.ui.dialog.PaymentDialogFragment.PaymentDialogListener;
import com.payoneer.checkout.ui.model.PaymentCard;
import com.payoneer.checkout.ui.model.PaymentSession;
import com.payoneer.checkout.ui.session.PaymentSessionListener;
import com.payoneer.checkout.ui.session.PaymentSessionService;
import com.payoneer.checkout.util.Resource;

import android.content.Context;
import android.util.Log;
import androidx.fragment.app.Fragment;

/**
 * PaymentListPresenter contains the business logic to load the payment session, delete saved accounts
 * and process payment requests.
 */
final class PaymentListPresenter implements PaymentSessionListener, PaymentServicePresenter {

    private final PaymentSessionService sessionService;
    private final CheckoutConfiguration configuration;

    private PaymentListViewModel listViewModel;
    private PaymentServiceViewModel serviceViewModel;

    private PaymentSession paymentSession;
    private PaymentService paymentService;
    private RequestData requestData;

    PaymentListPresenter(CheckoutConfiguration checkoutConfiguration) {
        this.configuration = checkoutConfiguration;
        sessionService = new PaymentSessionService();
        sessionService.setListener(this);
    }

    void setListViewModel(final PaymentListViewModel listViewModel) {
        this.listViewModel = listViewModel;
    }

    void onCheckoutListResume() {
        if (paymentService != null && paymentService.isPending()) {
            paymentService.resume();
        } else if (paymentSession == null) {
            loadPaymentSession();
        }
    }

    void onCheckoutListPause() {
        sessionService.onStop();

        if (paymentService != null) {
            paymentService.onStop();
        }
    }

    void loadPaymentSession() {
        this.paymentSession = null;
        listViewModel.showPaymentSession(Resource.LOADING, null, null);
        sessionService.loadPaymentSession(configuration, listViewModel.getApplicationContext());
    }

    void deletePaymentCard(final PaymentCard paymentCard) {
        String networkCode = paymentCard.getNetworkCode();
        String paymentMethod = paymentCard.getPaymentMethod();

        try {
            paymentService = loadPaymentService(networkCode, paymentMethod);
            paymentService.setPresenter(this);

            requestData = new RequestData(paymentSession.getListOperationType(), networkCode, paymentMethod,
                paymentCard.getOperationType(), paymentCard.getLinks(), new PaymentInputValues());

            paymentService.deleteAccount(requestData);
        } catch (PaymentException e) {
            listViewModel.closeWithCheckoutResult(CheckoutResultHelper.fromThrowable(e));
        }
    }

    void processPaymentCard(final PaymentCard paymentCard, final PaymentInputValues inputValues) {
        try {
            paymentService = loadPaymentService(paymentCard.getNetworkCode(), paymentCard.getPaymentMethod());
            paymentService.setPresenter(this);

            requestData = new RequestData(paymentSession.getListOperationType(), paymentCard.getNetworkCode(),
                paymentCard.getPaymentMethod(), paymentCard.getOperationType(),
                paymentCard.getLinks(), inputValues);

            paymentService.processPayment(requestData);
        } catch (PaymentException e) {
            listViewModel.closeWithCheckoutResult(CheckoutResultHelper.fromThrowable(e));
        }
    }

    @Override
    public void setPaymentServiceViewModel(final PaymentServiceViewModel serviceViewModel) {
        this.serviceViewModel = serviceViewModel;
    }

    @Override
    public void onPaymentSessionSuccess(PaymentSession session) {
        ListResult listResult = session.getListResult();
        Interaction interaction = listResult.getInteraction();

        if (Objects.equals(interaction.getCode(), PROCEED)) {
            handleLoadPaymentSessionProceed(session);
        } else {
            ErrorInfo errorInfo = new ErrorInfo(listResult.getResultInfo(), interaction);
            listViewModel.closeWithCheckoutResult(new CheckoutResult(errorInfo));
        }
    }

    @Override
    public void onPaymentSessionError(Throwable cause) {
        listViewModel.showPaymentSession(Resource.ERROR, null, null);

        CheckoutResult result = CheckoutResultHelper.fromThrowable(cause);
        if (result.isNetworkFailure()) {
            handleLoadPaymentSessionNetworkFailure(result);
        } else {
            listViewModel.closeWithCheckoutResult(result);
        }
    }

    private void closeWithErrorMessage(final String message) {
        CheckoutResult result = CheckoutResultHelper.fromErrorMessage(message);
        listViewModel.closeWithCheckoutResult(result);
    }

    private void handleLoadPaymentSessionNetworkFailure(final CheckoutResult checkoutResult) {
        listViewModel.showConnectionErrorDialog(new PaymentDialogListener() {
            @Override
            public void onPositiveButtonClicked() {
                loadPaymentSession();
            }

            @Override
            public void onNegativeButtonClicked() {
                listViewModel.closeWithCheckoutResult(checkoutResult);
            }

            @Override
            public void onDismissed() {
                listViewModel.closeWithCheckoutResult(checkoutResult);
            }
        });
    }

    private void handleLoadPaymentSessionProceed(final PaymentSession paymentSession) {
        if (paymentSession.isEmpty()) {
            closeWithErrorMessage("There are no payment methods available");
            return;
        }
        this.paymentSession = paymentSession;
        listViewModel.showPaymentSession(Resource.SUCCESS, paymentSession, null);
    }

    @Override
    public void showCustomFragment(final Fragment customFragment) {
        serviceViewModel.showCustomFragment(customFragment);
    }

    @Override
    public Context getApplicationContext() {
        return serviceViewModel.getApplicationContext();
    }

    @Override
    public void onProcessPaymentActive(final RequestData requestData, final boolean interruptible) {
        boolean transaction = CHARGE.equals(requestData.getListOperationType());
        listViewModel.onProcessPayment(transaction);

        if (transaction) {
            listViewModel.showTransactionProgress(true);
        } else {
            listViewModel.showPaymentListProgress(true);
        }
    }

    @Override
    public void onDeleteAccountActive(final RequestData requestData) {
        listViewModel.showPaymentListProgress(true);
    }

    @Override
    public void onProcessPaymentResult(final CheckoutResult result) {
        listViewModel.showTransactionProgress(false);
        listViewModel.showPaymentListProgress(false);

        if (UPDATE.equals(paymentSession.getListOperationType())) {
            handleUpdateCheckoutResult(result);
        } else {
            handleProcessPaymentResult(result);
        }
    }

    @Override
    public void onDeleteAccountResult(final CheckoutResult result) {
        listViewModel.showPaymentListProgress(false);

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
                showMessageAndPaymentSession(interaction, true);
                break;
            case TRY_OTHER_ACCOUNT:
            case TRY_OTHER_NETWORK:
                showMessageAndReloadPaymentSession(interaction, true);
                break;
            default:
                listViewModel.closeWithCheckoutResult(result);
        }
    }

    private void handleDeleteAccountNetworkFailure(final CheckoutResult checkoutResult) {
        listViewModel.showConnectionErrorDialog(new PaymentDialogListener() {
            @Override
            public void onPositiveButtonClicked() {
                paymentService.deleteAccount(requestData);
            }

            @Override
            public void onNegativeButtonClicked() {
            }

            @Override
            public void onDismissed() {
            }
        });
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
                showMessageAndResetPaymentSession(interaction, false);
                break;
            case OK:
                loadPaymentSession();
                break;
            default:
                listViewModel.closeWithCheckoutResult(result);
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
                showMessageAndReloadPaymentSession(interaction, false);
                break;
            default:
                listViewModel.closeWithCheckoutResult(result);
        }
    }

    private void handleProcessPaymentResult(final CheckoutResult result) {
        if (result.isProceed()) {
            listViewModel.closeWithCheckoutResult(result);
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
                showMessageAndReloadPaymentSession(interaction, false);
                break;
            case RETRY:
                Log.i("AAAA", "In here retry");
                showMessageAndPaymentSession(interaction, false);
                break;
            default:
                listViewModel.closeWithCheckoutResult(result);
        }
    }

    private void handleProcessNetworkFailure(final CheckoutResult result) {
        listViewModel.showConnectionErrorDialog(new PaymentDialogListener() {
            @Override
            public void onPositiveButtonClicked() {
                paymentService.processPayment(requestData);
            }

            @Override
            public void onNegativeButtonClicked() {
                listViewModel.closeWithCheckoutResult(result);
            }

            @Override
            public void onDismissed() {
                listViewModel.closeWithCheckoutResult(result);
            }
        });
    }

    private void showMessageAndResetPaymentSession(final Interaction interaction, final boolean deleteFlow) {
        InteractionMessage message = createInteractionMessage(interaction, deleteFlow);
        listViewModel.showInteractionDialog(null, message);
        listViewModel.showPaymentSession(Resource.SUCCESS, paymentSession, null);
    }

    private void showMessageAndReloadPaymentSession(final Interaction interaction, final boolean deleteFlow) {
        InteractionMessage message = createInteractionMessage(interaction, deleteFlow);
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
        loadPaymentSession();
        listViewModel.showInteractionDialog(listener, message);
    }

    private void showMessageAndPaymentSession(final Interaction interaction, final boolean deleteFlow) {
        InteractionMessage message = createInteractionMessage(interaction, deleteFlow);
        listViewModel.showInteractionDialog(null, message);
        listViewModel.showPaymentSession(Resource.SUCCESS, paymentSession, null);
    }

    private PaymentService loadPaymentService(final String code, final String paymentMethod) throws PaymentException {
        PaymentService service = PaymentServiceLookup.createService(code, paymentMethod);
        if (service == null) {
            throw new PaymentException("Missing PaymentService for: " + code + ", " + paymentMethod);
        }
        return service;
    }

    private InteractionMessage createInteractionMessage(final Interaction interaction, final boolean deleteFlow) {
        if (deleteFlow) {
            return InteractionMessage.fromDeleteFlow(interaction);
        }
        if (this.paymentSession == null) {
            return InteractionMessage.fromInteraction(interaction);
        }
        return InteractionMessage.fromOperationFlow(interaction, paymentSession.getListOperationType());
    }
}
