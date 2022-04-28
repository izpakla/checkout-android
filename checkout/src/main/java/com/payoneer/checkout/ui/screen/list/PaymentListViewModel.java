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
import static com.payoneer.checkout.redirect.RedirectService.INTERACTION_CODE;
import static com.payoneer.checkout.redirect.RedirectService.INTERACTION_REASON;

import java.net.URL;
import java.util.List;
import java.util.Objects;

import com.payoneer.checkout.CheckoutResult;
import com.payoneer.checkout.CheckoutResultHelper;
import com.payoneer.checkout.account.DeleteAccount;
import com.payoneer.checkout.account.PaymentAccountInteractor;
import com.payoneer.checkout.core.PaymentException;
import com.payoneer.checkout.core.PaymentLinkType;
import com.payoneer.checkout.localization.InteractionMessage;
import com.payoneer.checkout.model.ErrorInfo;
import com.payoneer.checkout.model.Interaction;
import com.payoneer.checkout.model.ListResult;
import com.payoneer.checkout.model.OperationResult;
import com.payoneer.checkout.model.Parameter;
import com.payoneer.checkout.model.Redirect;
import com.payoneer.checkout.payment.PaymentInputValues;
import com.payoneer.checkout.payment.PaymentServiceInteractor;
import com.payoneer.checkout.payment.ProcessPaymentData;
import com.payoneer.checkout.ui.dialog.PaymentDialogData;
import com.payoneer.checkout.ui.dialog.PaymentDialogFragment.PaymentDialogListener;
import com.payoneer.checkout.ui.model.PaymentCard;
import com.payoneer.checkout.ui.model.PaymentSession;
import com.payoneer.checkout.ui.model.PresetCard;
import com.payoneer.checkout.ui.session.PaymentSessionInteractor;
import com.payoneer.checkout.util.AppContextViewModel;
import com.payoneer.checkout.util.ContentEvent;
import com.payoneer.checkout.util.Event;
import com.payoneer.checkout.util.PaymentUtils;
import com.payoneer.checkout.util.Resource;

import android.content.Context;
import android.text.TextUtils;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

/**
 * PaymentListViewModel provides LiveData for the views and communicates with the interactors.
 * It operates within the lifecycle of the PaymentListActivity.
 */
final class PaymentListViewModel extends AppContextViewModel {

    private final MutableLiveData<Resource<PaymentSession>> showPaymentSession = new MutableLiveData<>();
    private final MutableLiveData<ContentEvent<CheckoutResult>> closeWithCheckoutResult = new MutableLiveData<>();
    private final MutableLiveData<ContentEvent<PaymentDialogData>> showPaymentDialog = new MutableLiveData<>();
    private final MutableLiveData<Event> showPaymentListFragment = new MutableLiveData<>();
    private final MutableLiveData<Event> showTransactionFragment = new MutableLiveData<>();
    private final MutableLiveData<ContentEvent<Fragment>> showCustomFragment = new MutableLiveData<>();
    private final MutableLiveData<ContentEvent<Boolean>> showPaymentListProgress = new MutableLiveData<>();
    private final MutableLiveData<ContentEvent<Boolean>> showTransactionProgress = new MutableLiveData<>();

    private final PaymentSessionInteractor sessionInteractor;
    private final PaymentServiceInteractor serviceInteractor;
    private final PaymentAccountInteractor accountInteractor;

    private PaymentSession paymentSession;
    private ProcessPaymentData processPaymentData;
    private DeleteAccount deleteAccount;
    private boolean paymentCardActionLocked;

    /**
     * Construct a new ProcessPaymentViewModel
     *
     * @param applicationContext context of the application
     * @param sessionInteractor provides interaction with the PaymentSessionService
     * @param serviceInteractor provides interaction with the PaymentService
     * @param accountInteractor provides interaction with the AccountService
     */
    PaymentListViewModel(final Context applicationContext, final PaymentSessionInteractor sessionInteractor,
        final PaymentServiceInteractor serviceInteractor, final PaymentAccountInteractor accountInteractor) {
        super(applicationContext);

        this.sessionInteractor = sessionInteractor;
        initPaymentSessionObserver(sessionInteractor);

        this.serviceInteractor = serviceInteractor;
        initPaymentServiceObserver(serviceInteractor);

        this.accountInteractor = accountInteractor;
        initPaymentAccountObserver(accountInteractor);
    }

    LiveData<Resource<PaymentSession>> showPaymentSession() {
        return showPaymentSession;
    }

    LiveData<ContentEvent<CheckoutResult>> closeWithCheckoutResult() {
        return closeWithCheckoutResult;
    }

    LiveData<ContentEvent<PaymentDialogData>> showPaymentDialog() {
        return showPaymentDialog;
    }

    LiveData<Event> showPaymentListFragment() {
        return showPaymentListFragment;
    }

    LiveData<Event> showTransactionFragment() {
        return showTransactionFragment;
    }

    LiveData<ContentEvent<Boolean>> showPaymentListProgress() {
        return showPaymentListProgress;
    }

    LiveData<ContentEvent<Boolean>> showTransactionProgress() {
        return showTransactionProgress;
    }

    LiveData<ContentEvent<Fragment>> showCustomFragment() {
        return showCustomFragment;
    }

    void onPaymentListResume() {
        if (!serviceInteractor.onResume()) {
            loadPaymentSession();
        }
    }

    void onPaymentListPause() {
        sessionInteractor.onStop();
        serviceInteractor.onStop();
        accountInteractor.onStop();
    }

    void loadPaymentSession() {
        this.paymentSession = null;
        setShowPaymentSession(Resource.LOADING, null);
        sessionInteractor.loadPaymentSession(getApplicationContext());
    }

    void processPaymentCard(final PaymentCard paymentCard, final PaymentInputValues inputValues) {
        // ignore multiple click events
        if (!lockPaymentCardAction()) {
            return;
        }
        if (paymentCard instanceof PresetCard) {
            processPresetCard((PresetCard) paymentCard);
        } else {
            String networkCode = paymentCard.getNetworkCode();
            String paymentMethod = paymentCard.getPaymentMethod();
            try {
                serviceInteractor.loadPaymentService(networkCode, paymentMethod);
                processPaymentData = new ProcessPaymentData(paymentSession.getListOperationType(), networkCode, paymentMethod,
                    paymentCard.getOperationType(),
                    paymentCard.getLinks(), inputValues);
                serviceInteractor.processPayment(processPaymentData, getApplicationContext());
            } catch (PaymentException e) {
                setCloseWithCheckoutResult(CheckoutResultHelper.fromThrowable(e));
            }
        }
        unlockPaymentCardAction();
    }

    void deletePaymentCard(final PaymentCard paymentCard) {
        // ignore multiple click events
        if (!lockPaymentCardAction()) {
            return;
        }
        String networkCode = paymentCard.getNetworkCode();
        String paymentMethod = paymentCard.getPaymentMethod();

        try {
            serviceInteractor.loadPaymentService(networkCode, paymentMethod);
            URL url = paymentCard.getLink(PaymentLinkType.SELF);
            deleteAccount = new DeleteAccount(url);
            accountInteractor.deleteAccount(deleteAccount, getApplicationContext());
        } catch (PaymentException e) {
            setCloseWithCheckoutResult(CheckoutResultHelper.fromThrowable(e));
        }
        unlockPaymentCardAction();
    }

    private void initPaymentSessionObserver(final PaymentSessionInteractor interactor) {
        interactor.setObserver(new PaymentSessionInteractor.Observer() {
            @Override
            public void onPaymentSessionSuccess(final PaymentSession paymentSession) {
                handlePaymentSessionSuccess(paymentSession);
            }

            @Override
            public void onPaymentSessionError(final CheckoutResult checkoutResult) {
                handlePaymentSessionError(checkoutResult);
            }
        });
    }

    private void initPaymentAccountObserver(final PaymentAccountInteractor interactor) {
        interactor.setObserver(new PaymentAccountInteractor.Observer() {

            @Override
            public void onDeleteAccountResult(final CheckoutResult checkoutResult) {
                handleOnDeleteAccountResult(checkoutResult);
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
            public void onProcessPaymentResult(final CheckoutResult checkoutResult) {
                handleOnProcessPaymentResult(checkoutResult);
            }
        });
    }

    private void setShowCustomFragment(final Fragment customFragment) {
        showCustomFragment.setValue(new ContentEvent<>(customFragment));
    }

    private void setCloseWithCheckoutResult(final CheckoutResult checkoutResult) {
        closeWithCheckoutResult.setValue(new ContentEvent<>(checkoutResult));
    }

    private void setShowConnectionErrorDialog(final PaymentDialogListener listener) {
        PaymentDialogData data = PaymentDialogData.connectionErrorDialog(listener);
        showPaymentDialog.setValue(new ContentEvent<>(data));
    }

    private void setShowInteractionDialog(final PaymentDialogListener listener, final InteractionMessage interactionMessage) {
        PaymentDialogData data = PaymentDialogData.interactionDialog(listener, interactionMessage);
        showPaymentDialog.setValue(new ContentEvent<>(data));
    }

    private void setShowPaymentSession(final int status, final PaymentSession paymentSession) {
        showPaymentListFragment.setValue(new Event());
        switch (status) {
            case Resource.SUCCESS:
                showPaymentSession.setValue(Resource.success(paymentSession));
                break;
            case Resource.LOADING:
                showPaymentSession.setValue(Resource.loading());
                break;
            case Resource.ERROR:
                showPaymentSession.setValue(Resource.error(null));
        }
    }

    private void setShowProcessPaymentProgress(final boolean visible) {
        boolean transaction = CHARGE.equals(paymentSession.getListOperationType());

        if (transaction) {
            showTransactionFragment.setValue(new Event());
            showTransactionProgress.setValue(new ContentEvent<>(visible));
        } else {
            showPaymentListFragment.setValue(new Event());
            showPaymentListProgress.setValue(new ContentEvent<>(visible));
        }
    }

    private void setShowDeleteAccountProgress(final boolean visible) {
        showPaymentListFragment.setValue(new Event());
        showPaymentListProgress.setValue(new ContentEvent<>(visible));
    }


    private void handlePaymentSessionSuccess(final PaymentSession session) {
        ListResult listResult = session.getListResult();
        Interaction interaction = listResult.getInteraction();

        if (Objects.equals(interaction.getCode(), PROCEED)) {
            handleLoadPaymentSessionProceed(session);
        } else {
            ErrorInfo errorInfo = new ErrorInfo(listResult.getResultInfo(), interaction);
            setCloseWithCheckoutResult(new CheckoutResult(errorInfo));
        }
    }

    private void handlePaymentSessionError(final CheckoutResult result) {
        setShowPaymentSession(Resource.ERROR, null);

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

    private void handleLoadPaymentSessionProceed(final PaymentSession paymentSession) {
        if (paymentSession.isEmpty()) {
            closeWithErrorMessage("There are no payment methods available");
            return;
        }
        this.paymentSession = paymentSession;
        setShowPaymentSession(Resource.SUCCESS, paymentSession);
    }

    private void handleOnProcessPaymentResult(final CheckoutResult result) {
        setShowProcessPaymentProgress(false);

        if (UPDATE.equals(processPaymentData.getListOperationType())) {
            handleUpdateCheckoutResult(result);
        } else {
            handleProcessPaymentResult(result);
        }
    }

    private void handleOnDeleteAccountResult(final CheckoutResult result) {
        setShowDeleteAccountProgress(false);

        if (result.isNetworkFailure()) {
            handleDeleteAccountNetworkFailure();
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
                setCloseWithCheckoutResult(result);
        }
    }

    private void handleDeleteAccountNetworkFailure() {
        setShowConnectionErrorDialog(new PaymentDialogListener() {
            @Override
            public void onPositiveButtonClicked() {
                accountInteractor.deleteAccount(deleteAccount, getApplicationContext());
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
                setCloseWithCheckoutResult(result);
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
                setCloseWithCheckoutResult(result);
        }
    }

    private void handleProcessPaymentResult(final CheckoutResult result) {
        if (result.isProceed()) {
            setCloseWithCheckoutResult(result);
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
                showMessageAndPaymentSession(interaction, false);
                break;
            default:
                setCloseWithCheckoutResult(result);
        }
    }

    private void handleProcessNetworkFailure(final CheckoutResult result) {
        setShowConnectionErrorDialog(new PaymentDialogListener() {
            @Override
            public void onPositiveButtonClicked() {
                serviceInteractor.processPayment(processPaymentData, getApplicationContext());
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

    private void showMessageAndResetPaymentSession(final Interaction interaction, final boolean deleteFlow) {
        InteractionMessage message = createInteractionMessage(interaction, deleteFlow);
        setShowInteractionDialog(null, message);
        setShowPaymentSession(Resource.SUCCESS, paymentSession);
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
        setShowInteractionDialog(listener, message);
    }

    private void showMessageAndPaymentSession(final Interaction interaction, final boolean deleteFlow) {
        InteractionMessage message = createInteractionMessage(interaction, deleteFlow);
        setShowInteractionDialog(null, message);
        setShowPaymentSession(Resource.SUCCESS, paymentSession);
    }

    private void processPresetCard(final PresetCard card) {
        Redirect redirect = card.getPresetAccount().getRedirect();
        List<Parameter> parameters = redirect.getParameters();

        String code = PaymentUtils.getParameterValue(INTERACTION_CODE, parameters);
        String reason = PaymentUtils.getParameterValue(INTERACTION_REASON, parameters);
        if (TextUtils.isEmpty(code) || TextUtils.isEmpty(reason)) {
            closeWithErrorMessage("Missing Interaction code and reason inside PresetAccount.redirect");
            return;
        }
        OperationResult result = new OperationResult();
        result.setResultInfo("PresetAccount selected");
        result.setInteraction(new Interaction(code, reason));
        result.setRedirect(redirect);
        setCloseWithCheckoutResult(new CheckoutResult(result));
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

    private boolean lockPaymentCardAction() {
        return !paymentCardActionLocked && (paymentCardActionLocked = true);
    }

    private void unlockPaymentCardAction() {
        this.paymentCardActionLocked = false;
    }
}