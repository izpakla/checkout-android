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
import static com.payoneer.checkout.localization.LocalizationKey.CHARGE_INTERRUPTED;
import static com.payoneer.checkout.ui.page.ChargePaymentActivity.TYPE_CHARGE_PRESET_ACCOUNT;

import java.util.Objects;

import com.payoneer.checkout.CheckoutConfiguration;
import com.payoneer.checkout.CheckoutResult;
import com.payoneer.checkout.CheckoutResultHelper;
import com.payoneer.checkout.core.PaymentException;
import com.payoneer.checkout.localization.Localization;
import com.payoneer.checkout.model.ErrorInfo;
import com.payoneer.checkout.model.Interaction;
import com.payoneer.checkout.model.InteractionCode;
import com.payoneer.checkout.model.ListResult;
import com.payoneer.checkout.model.PresetAccount;
import com.payoneer.checkout.payment.PaymentInputValues;
import com.payoneer.checkout.payment.PaymentRequest;
import com.payoneer.checkout.payment.PaymentService;
import com.payoneer.checkout.payment.PaymentServiceController;
import com.payoneer.checkout.ui.dialog.PaymentDialogFragment;
import com.payoneer.checkout.ui.dialog.PaymentDialogFragment.PaymentDialogListener;
import com.payoneer.checkout.ui.model.PaymentSession;
import com.payoneer.checkout.ui.session.PaymentSessionListener;
import com.payoneer.checkout.ui.session.PaymentSessionService;

import android.content.Context;

/**
 * The ChargePaymentPresenter takes care of posting the operation to the Payment API.
 * First this presenter will load the list, checks if the operation is present and then post the operation to the Payment API.
 */
final class ChargePaymentPresenter extends BasePaymentPresenter implements PaymentSessionListener, PaymentServiceController {

    private final PaymentSessionService sessionService;
    private PaymentSession session;
    private PaymentRequest paymentRequest;
    private PaymentService paymentService;
    private int chargeType;

    /**
     * Create a new ChargePaymentPresenter
     *
     * @param view the BasePaymentView displaying payment information
     */
    ChargePaymentPresenter(CheckoutConfiguration configuration, BasePaymentView view) {
        super(configuration, view);
        sessionService = new PaymentSessionService();
        sessionService.setListener(this);
    }

    void onStart(PaymentRequest paymentRequest, int chargeType) {
        this.chargeType = chargeType;
        if (chargeType == ChargePaymentActivity.TYPE_CHARGE_OPERATION) {
            this.paymentRequest = paymentRequest;
        }
        setState(STARTED);

        if (paymentService != null && paymentService.isProcessing()) {
            paymentService.resumeProcessing();
        } else {
            loadPaymentSession();
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
    public void onPaymentSessionSuccess(PaymentSession session) {
        ListResult listResult = session.getListResult();
        Interaction interaction = listResult.getInteraction();
        if (Objects.equals(InteractionCode.PROCEED, interaction.getCode())) {
            handleLoadSessionProceed(session);
        } else {
            ErrorInfo errorInfo = new ErrorInfo(listResult.getResultInfo(), interaction);
            CheckoutResult result = new CheckoutResult(errorInfo, null);
            closeWithErrorCode(result);
        }
    }

    @Override
    public Context getContext() {
        return view.getActivity();
    }

    @Override
    public void onPaymentSessionError(Throwable cause) {
        handleLoadingError(cause);
    }

    @Override
    public void showProgress(boolean visible) {
        view.showProgress(visible);
    }

    @Override
    public void onProcessPaymentResult(int resultCode, CheckoutResult result) {
        setState(STARTED);
        switch (resultCode) {
            case RESULT_CODE_PROCEED:
                closeWithProceedCode(result);
                break;
            case RESULT_CODE_ERROR:
                handleProcessPaymentError(result);
                break;
        }
    }

    @Override
    public void onDeleteAccountResult(int resultCode, CheckoutResult result) {
    }

    private void processPayment() {
        try {
            paymentService = loadNetworkService(paymentRequest.getNetworkCode(), paymentRequest.getPaymentMethod());
            paymentService.setController(this);
            processPayment(paymentRequest);
        } catch (PaymentException e) {
            closeWithErrorCode(CheckoutResultHelper.fromThrowable(e));
        }
    }

    boolean onBackPressed() {
        view.showWarningMessage(Localization.translate(CHARGE_INTERRUPTED));
        return true;
    }

    private void handleLoadSessionProceed(PaymentSession session) {
        if (chargeType == TYPE_CHARGE_PRESET_ACCOUNT) {
            PresetAccount account = session.getListResult().getPresetAccount();
            if (account == null) {
                closeWithErrorCode("PresetAccount not found in ListResult");
                return;
            }
            this.paymentRequest = PaymentRequest.from(account, new PaymentInputValues());
        }
        this.session = session;
        processPayment();
    }

    private void handleLoadingError(Throwable cause) {
        CheckoutResult result = CheckoutResultHelper.fromThrowable(InteractionCode.ABORT, cause);

        if (result.isNetworkFailure()) {
            handleLoadingNetworkFailure(result);
        } else {
            closeWithErrorCode(result);
        }
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
                showMessageAndCloseWithErrorCode(result);
                break;
            default:
                closeWithErrorCode(result);
        }
    }

    private void handleProcessNetworkFailure(final CheckoutResult result) {
        view.showConnectionErrorDialog(new PaymentDialogListener() {
            @Override
            public void onPositiveButtonClicked() {
                processPayment(paymentRequest);
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

    private void processPayment(final PaymentRequest paymentRequest) {
        setState(PROCESS);
        paymentService.processPayment(paymentRequest, view.getActivity());
    }

    private void showMessageAndCloseWithErrorCode(CheckoutResult result) {
        Interaction interaction = result.getInteraction();
        view.setCheckoutResult(RESULT_CODE_ERROR, result);
        PaymentDialogFragment.PaymentDialogListener listener = new PaymentDialogFragment.PaymentDialogListener() {
            @Override
            public void onPositiveButtonClicked() {
                view.close();
            }

            @Override
            public void onNegativeButtonClicked() {
                view.close();
            }

            @Override
            public void onDismissed() {
                view.close();
            }
        };
        view.showInteractionDialog(createInteractionMessage(interaction, session), listener);
    }

    private void loadPaymentSession() {
        this.session = null;
        view.showProgress(true);
        sessionService.loadPaymentSession(configuration, view.getActivity());
    }
}
