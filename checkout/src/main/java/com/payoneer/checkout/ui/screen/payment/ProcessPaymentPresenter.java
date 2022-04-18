/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.screen.payment;

import static com.payoneer.checkout.model.InteractionCode.PROCEED;

import java.util.Objects;

import com.payoneer.checkout.CheckoutConfiguration;
import com.payoneer.checkout.CheckoutResult;
import com.payoneer.checkout.CheckoutResultHelper;
import com.payoneer.checkout.core.PaymentException;
import com.payoneer.checkout.localization.InteractionMessage;
import com.payoneer.checkout.model.ErrorInfo;
import com.payoneer.checkout.model.Interaction;
import com.payoneer.checkout.model.InteractionCode;
import com.payoneer.checkout.model.ListResult;
import com.payoneer.checkout.model.PresetAccount;
import com.payoneer.checkout.payment.PaymentInputValues;
import com.payoneer.checkout.payment.PaymentService;
import com.payoneer.checkout.payment.PaymentServiceLookup;
import com.payoneer.checkout.payment.PaymentServicePresenter;
import com.payoneer.checkout.payment.PaymentServiceViewModel;
import com.payoneer.checkout.payment.RequestData;
import com.payoneer.checkout.ui.dialog.PaymentDialogFragment;
import com.payoneer.checkout.ui.dialog.PaymentDialogFragment.PaymentDialogListener;
import com.payoneer.checkout.ui.model.PaymentSession;
import com.payoneer.checkout.ui.session.PaymentSessionListener;
import com.payoneer.checkout.ui.session.PaymentSessionService;

import android.content.Context;
import androidx.fragment.app.Fragment;

/**
 * ProcessPaymentPresenter contains the business logic to process and finalize a PresetAccount.
 */
final class ProcessPaymentPresenter implements PaymentSessionListener, PaymentServicePresenter {

    private final PaymentSessionService sessionService;
    private final CheckoutConfiguration configuration;

    private ProcessPaymentViewModel paymentViewModel;
    private PaymentServiceViewModel serviceViewModel;

    private PaymentSession paymentSession;
    private PaymentService paymentService;
    private RequestData requestData;

    ProcessPaymentPresenter(CheckoutConfiguration checkoutConfiguration) {
        this.configuration = checkoutConfiguration;
        sessionService = new PaymentSessionService();
        sessionService.setListener(this);
    }

    void setPaymentViewModel(final ProcessPaymentViewModel paymentViewModel) {
        this.paymentViewModel = paymentViewModel;
    }

    void onProcessPaymentResume() {
        if (paymentService != null && paymentService.isPending()) {
            paymentService.resume();
        } else if (paymentSession == null) {
            loadPaymentSession();
        }
    }

    void onProcessPaymentPause() {
        sessionService.onStop();

        if (paymentService != null) {
            paymentService.onStop();
        }
    }

    void loadPaymentSession() {
        this.paymentSession = null;
        paymentViewModel.showProgress(true);
        sessionService.loadPaymentSession(configuration, paymentViewModel.getApplicationContext());
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
            paymentViewModel.closeWithCheckoutResult(new CheckoutResult(errorInfo));
        }
    }

    @Override
    public void onPaymentSessionError(Throwable cause) {
        paymentViewModel.showProgress(false);

        CheckoutResult result = CheckoutResultHelper.fromThrowable(cause);
        if (result.isNetworkFailure()) {
            handleLoadPaymentSessionNetworkFailure(result);
        } else {
            paymentViewModel.closeWithCheckoutResult(result);
        }
    }

    private void closeWithErrorMessage(final String message) {
        CheckoutResult result = CheckoutResultHelper.fromErrorMessage(message);
        paymentViewModel.closeWithCheckoutResult(result);
    }

    private void handleLoadPaymentSessionNetworkFailure(final CheckoutResult checkoutResult) {
        paymentViewModel.showConnectionErrorDialog(new PaymentDialogListener() {
            @Override
            public void onPositiveButtonClicked() {
                loadPaymentSession();
            }

            @Override
            public void onNegativeButtonClicked() {
                paymentViewModel.closeWithCheckoutResult(checkoutResult);
            }

            @Override
            public void onDismissed() {
                paymentViewModel.closeWithCheckoutResult(checkoutResult);
            }
        });
    }

    private void handleLoadPaymentSessionProceed(PaymentSession paymentSession) {
        PresetAccount account = paymentSession.getListResult().getPresetAccount();
        if (account == null) {
            closeWithErrorMessage("PresetAccount not found in ListResult");
            return;
        }
        this.paymentSession = paymentSession;
        this.requestData = createRequestData(account);
        processPayment();
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
        paymentViewModel.showProcessPayment();
        paymentViewModel.showProgress(true);
    }

    @Override
    public void onDeleteAccountActive(final RequestData requestData) {
    }

    @Override
    public void onProcessPaymentResult(final RequestData requestData, final CheckoutResult result) {
        paymentViewModel.showProgress(false);
        if (result.isProceed()) {
            paymentViewModel.closeWithCheckoutResult(result);
        } else {
            handleProcessPaymentError(result);
        }
    }

    @Override
    public void onDeleteAccountResult(final RequestData requestData, final CheckoutResult result) {
    }

    private void processPayment() {
        try {
            paymentService = loadPaymentService(requestData.getNetworkCode(), requestData.getPaymentMethod());
            paymentService.setPresenter(this);
            paymentService.processPayment(requestData);
        } catch (PaymentException e) {
            paymentViewModel.closeWithCheckoutResult(CheckoutResultHelper.fromThrowable(e));
        }
    }

    private void handleProcessPaymentError(CheckoutResult result) {
        if (result.isNetworkFailure()) {
            handleProcessNetworkFailure(result);
            return;
        }
        Interaction interaction = result.getInteraction();
        switch (interaction.getCode()) {
            case InteractionCode.TRY_OTHER_ACCOUNT:
            case InteractionCode.TRY_OTHER_NETWORK:
            case InteractionCode.RETRY:
                showMessageAndCloseWithCheckoutResult(result);
                break;
            default:
                paymentViewModel.closeWithCheckoutResult(result);
        }
    }

    private void handleProcessNetworkFailure(final CheckoutResult result) {
        paymentViewModel.showConnectionErrorDialog(new PaymentDialogListener() {
            @Override
            public void onPositiveButtonClicked() {
                paymentService.processPayment(requestData);
            }

            @Override
            public void onNegativeButtonClicked() {
                paymentViewModel.closeWithCheckoutResult(result);
            }

            @Override
            public void onDismissed() {
                paymentViewModel.closeWithCheckoutResult(result);
            }
        });
    }

    private PaymentService loadPaymentService(final String code, final String paymentMethod) throws PaymentException {
        PaymentService service = PaymentServiceLookup.createService(code, paymentMethod);
        if (service == null) {
            throw new PaymentException("Missing PaymentService for: " + code + ", " + paymentMethod);
        }
        return service;
    }

    private void showMessageAndCloseWithCheckoutResult(final CheckoutResult result) {
        Interaction interaction = result.getInteraction();
        PaymentDialogFragment.PaymentDialogListener listener = new PaymentDialogFragment.PaymentDialogListener() {
            @Override
            public void onPositiveButtonClicked() {
                paymentViewModel.closeWithCheckoutResult(result);
            }

            @Override
            public void onNegativeButtonClicked() {
                paymentViewModel.closeWithCheckoutResult(result);
            }

            @Override
            public void onDismissed() {
                paymentViewModel.closeWithCheckoutResult(result);
            }
        };
        paymentViewModel.showInteractionDialog(listener, createInteractionMessage(interaction));
    }

    private RequestData createRequestData(final PresetAccount presetAccount) {
        return new RequestData(paymentSession.getListOperationType(),
            presetAccount.getCode(),
            presetAccount.getMethod(),
            presetAccount.getOperationType(),
            presetAccount.getLinks(),
            new PaymentInputValues());
    }

    private InteractionMessage createInteractionMessage(final Interaction interaction) {
        if (this.paymentSession == null) {
            return InteractionMessage.fromInteraction(interaction);
        }
        return InteractionMessage.fromOperationFlow(interaction, paymentSession.getListOperationType());
    }
}
