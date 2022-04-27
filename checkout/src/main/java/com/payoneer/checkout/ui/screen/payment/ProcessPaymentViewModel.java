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
import com.payoneer.checkout.payment.PaymentServiceInteractor;
import com.payoneer.checkout.payment.RequestData;
import com.payoneer.checkout.ui.dialog.PaymentDialogData;
import com.payoneer.checkout.ui.dialog.PaymentDialogFragment;
import com.payoneer.checkout.ui.dialog.PaymentDialogFragment.PaymentDialogListener;
import com.payoneer.checkout.ui.model.PaymentSession;
import com.payoneer.checkout.ui.session.PaymentSessionInteractor;
import com.payoneer.checkout.util.AppContextViewModel;
import com.payoneer.checkout.util.ContentEvent;
import com.payoneer.checkout.util.Event;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

/**
 * ProcessPaymentViewModel provides LiveData for the views and communicates with the interactors.
 * It operates within the lifecycle of the ProcessPaymentActivity.
 */
final class ProcessPaymentViewModel extends AppContextViewModel {

    private final MutableLiveData<ContentEvent<Boolean>> showProcessPaymentProgress = new MutableLiveData<>();
    private final MutableLiveData<ContentEvent<CheckoutResult>> closeWithCheckoutResult = new MutableLiveData<>();
    private final MutableLiveData<ContentEvent<PaymentDialogData>> showPaymentDialog = new MutableLiveData<>();
    private final MutableLiveData<Event> showProcessPaymentFragment = new MutableLiveData<>();
    private final MutableLiveData<ContentEvent<Fragment>> showCustomFragment = new MutableLiveData<>();

    private final PaymentSessionInteractor sessionInteractor;
    private final PaymentServiceInteractor serviceInteractor;

    private PaymentSession paymentSession;
    private RequestData requestData;

    /**
     * Construct a new ProcessPaymentViewModel
     *
     * @param applicationContext context of the application
     * @param sessionInteractor provides interaction with the PaymentSessionService
     * @param serviceInteractor provides interaction with the PaymentService
     */
    ProcessPaymentViewModel(final Context applicationContext, final PaymentSessionInteractor sessionInteractor,
        final PaymentServiceInteractor serviceInteractor) {
        super(applicationContext);

        this.sessionInteractor = sessionInteractor;
        initPaymentSessionObserver(sessionInteractor);

        this.serviceInteractor = serviceInteractor;
        initPaymentServiceObserver(serviceInteractor);
    }

    LiveData<ContentEvent<Boolean>> showProcessPaymentProgress() {
        return showProcessPaymentProgress;
    }

    LiveData<ContentEvent<CheckoutResult>> closeWithCheckoutResult() {
        return closeWithCheckoutResult;
    }

    LiveData<ContentEvent<PaymentDialogData>> showPaymentDialog() {
        return showPaymentDialog;
    }

    LiveData<Event> showProcessPaymentFragment() {
        return showProcessPaymentFragment;
    }

    LiveData<ContentEvent<Fragment>> showCustomFragment() {
        return showCustomFragment;
    }

    private void initPaymentSessionObserver(final PaymentSessionInteractor interactor) {
        interactor.setObserver(new PaymentSessionInteractor.Observer() {
            @Override
            public void onPaymentSessionSuccess(final PaymentSession paymentSession) {
                handleOnPaymentSessionSuccess(paymentSession);
            }

            @Override
            public void onPaymentSessionError(final CheckoutResult checkoutResult) {
                handleOnPaymentSessionError(checkoutResult);
            }
        });
    }

    private void initPaymentServiceObserver(final PaymentServiceInteractor interactor) {
        interactor.setObserver(new PaymentServiceInteractor.Observer() {
            @Override
            public void showFragment(final Fragment fragment) {
                setShowCustomFragment(fragment);
            }

            @Override
            public void onProcessPaymentActive() {
                setShowProcessPaymentProgress(true);
            }

            @Override
            public void onDeleteAccountActive() {
                // delete account is not implemented by this viewmodel
            }

            @Override
            public void onProcessPaymentResult(final CheckoutResult checkoutResult) {
                handleOnProcessPaymentResult(checkoutResult);
            }

            @Override
            public void onDeleteAccountResult(final CheckoutResult checkoutResult) {
                // delete account is not implemented by this viewmodel
            }
        });
    }

    private void setShowCustomFragment(final Fragment customFragment) {
        showCustomFragment.setValue(new ContentEvent<>(customFragment));
    }

    private void setCloseWithCheckoutResult(final CheckoutResult checkoutResult) {
        closeWithCheckoutResult.setValue(new ContentEvent<>(checkoutResult));
    }

    private void setShowProcessPaymentProgress(final Boolean visible) {
        showProcessPaymentFragment.setValue(new Event());
        showProcessPaymentProgress.setValue(new ContentEvent<>(visible));
    }

    private void setShowConnectionErrorDialog(final PaymentDialogListener listener) {
        PaymentDialogData data = PaymentDialogData.connectionErrorDialog(listener);
        showPaymentDialog.setValue(new ContentEvent<>(data));
    }

    private void setShowInteractionDialog(final PaymentDialogListener listener, final InteractionMessage interactionMessage) {
        PaymentDialogData data = PaymentDialogData.interactionDialog(listener, interactionMessage);
        showPaymentDialog.setValue(new ContentEvent<>(data));
    }

    void onProcessPaymentResume() {
        if (!serviceInteractor.onResume()) {
            loadPaymentSession();
        }
    }

    void onProcessPaymentPause() {
        sessionInteractor.onStop();
        serviceInteractor.onStop();
    }

    void loadPaymentSession() {
        this.paymentSession = null;
        setShowProcessPaymentProgress(true);
        sessionInteractor.loadPaymentSession(getApplicationContext());
    }

    private void handleOnPaymentSessionSuccess(PaymentSession session) {
        ListResult listResult = session.getListResult();
        Interaction interaction = listResult.getInteraction();

        if (Objects.equals(interaction.getCode(), PROCEED)) {
            handleLoadPaymentSessionProceed(session);
        } else {
            ErrorInfo errorInfo = new ErrorInfo(listResult.getResultInfo(), interaction);
            setCloseWithCheckoutResult(new CheckoutResult(errorInfo));
        }
    }

    private void handleOnPaymentSessionError(final CheckoutResult result) {
        setShowProcessPaymentProgress(false);

        if (result.isNetworkFailure()) {
            handleLoadPaymentSessionNetworkFailure(result);
        } else {
            setCloseWithCheckoutResult(result);
        }
    }

    private void closeWithErrorMessage(final String message) {
        CheckoutResult result = CheckoutResultHelper.fromErrorMessage(message);
        setCloseWithCheckoutResult(result);
    }

    private void handleLoadPaymentSessionNetworkFailure(final CheckoutResult checkoutResult) {
        setShowConnectionErrorDialog(new PaymentDialogListener() {
            @Override
            public void onPositiveButtonClicked() {
                loadPaymentSession();
            }

            @Override
            public void onNegativeButtonClicked() {
                setCloseWithCheckoutResult(checkoutResult);
            }

            @Override
            public void onDismissed() {
                setCloseWithCheckoutResult(checkoutResult);
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

    private void handleOnProcessPaymentResult(final CheckoutResult result) {
        setShowProcessPaymentProgress(false);
        if (result.isProceed()) {
            setCloseWithCheckoutResult(result);
        } else {
            handleProcessPaymentError(result);
        }
    }

    private void processPayment() {
        try {
            serviceInteractor.loadPaymentService(requestData.getNetworkCode(), requestData.getPaymentMethod());
            serviceInteractor.processPayment(requestData, getApplicationContext());
        } catch (PaymentException e) {
            setCloseWithCheckoutResult(CheckoutResultHelper.fromThrowable(e));
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
                setCloseWithCheckoutResult(result);
        }
    }

    private void handleProcessNetworkFailure(final CheckoutResult result) {
        setShowConnectionErrorDialog(new PaymentDialogListener() {
            @Override
            public void onPositiveButtonClicked() {
                serviceInteractor.processPayment(requestData, getApplicationContext());
            }

            @Override
            public void onNegativeButtonClicked() {
                setCloseWithCheckoutResult(result);
            }

            @Override
            public void onDismissed() {
                setCloseWithCheckoutResult(result);
            }
        });
    }

    private void showMessageAndCloseWithCheckoutResult(final CheckoutResult result) {
        Interaction interaction = result.getInteraction();
        PaymentDialogFragment.PaymentDialogListener listener = new PaymentDialogFragment.PaymentDialogListener() {
            @Override
            public void onPositiveButtonClicked() {
                setCloseWithCheckoutResult(result);
            }

            @Override
            public void onNegativeButtonClicked() {
                setCloseWithCheckoutResult(result);
            }

            @Override
            public void onDismissed() {
                setCloseWithCheckoutResult(result);
            }
        };
        setShowInteractionDialog(listener, createInteractionMessage(interaction));
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
