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

import java.util.List;
import java.util.Objects;

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
import com.payoneer.checkout.payment.PaymentServiceInteractor;
import com.payoneer.checkout.payment.RequestData;
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
 * PaymentListViewModel provides LiveData between the PaymentListActivity and PaymentListPresenter.
 * It operates within the lifecycle of the PaymentListActivity.
 */
final class PaymentServiceFlowController {

    private final PaymentListViewModel viewModel;
    private final PaymentServiceInteractor interactor;
    private RequestData requestData;

    PaymentServiceFlowController(final PaymentListViewModel viewModel, final PaymentServiceInteractor interactor) {
        this.viewModel = viewModel;
        this.interactor = interactor;
        initPaymentServiceObserver(interactor);
    }

    void deleteAccount(final RequestData requestData) {
        try {
            interactor.loadPaymentService(requestData.getNetworkCode(), requestData.getPaymentMethod());
            this.requestData = requestData;
            interactor.deleteAccount(requestData, viewModel.getApplicationContext());
        } catch (PaymentException e) {
            viewModel.setCloseWithCheckoutResult(CheckoutResultHelper.fromThrowable(e));
        }
    }

    void processPayment(final RequestData requestData) {
        try {
            interactor.loadPaymentService(requestData.getNetworkCode(), requestData.getPaymentMethod());
            interactor.processPayment(requestData, viewModel.getApplicationContext());
        } catch (PaymentException e) {
            viewModel.setCloseWithCheckoutResult(CheckoutResultHelper.fromThrowable(e));
        }
    }

    private void initPaymentServiceObserver(final PaymentServiceInteractor interactor) {
        interactor.setObserver(new PaymentServiceInteractor.Observer() {
            @Override
            public void showCustomFragment(final Fragment customFragment) {
                viewModel.setShowCustomFragment(customFragment);
            }

            @Override
            public void onProcessPaymentActive() {
                viewModel.setShowProcessPaymentProgress(true);
            }

            @Override
            public void onDeleteAccountActive() {
                viewModel.setShowDeleteAccountProgress(true);
            }

            @Override
            public void onProcessPaymentResult(final CheckoutResult checkoutResult) {
                handleOnProcessPaymentResult(checkoutResult);
            }

            @Override
            public void onDeleteAccountResult(final CheckoutResult checkoutResult) {
                handleOnDeleteAccountResult(checkoutResult);
            }
        });
    }

    private void closeWithErrorMessage(final String message) {
        CheckoutResult result = CheckoutResultHelper.fromErrorMessage(message);
        viewModel.setCloseWithCheckoutResult(result);
    }

    private void handleOnProcessPaymentResult(final CheckoutResult result) {
        viewModel.setShowProcessPaymentProgress(false);

        if (UPDATE.equals(requestData.getListOperationType())) {
            handleUpdateCheckoutResult(result);
        } else {
            handleProcessPaymentResult(result);
        }
    }

    private void handleOnDeleteAccountResult(final CheckoutResult result) {
        viewModel.setShowDeleteAccountProgress(false);

        if (result.isNetworkFailure()) {
            handleDeleteAccountNetworkFailure();
            return;
        }
        Interaction interaction = result.getInteraction();
        switch (interaction.getCode()) {
            case PROCEED:
            case RELOAD:
                viewModel.loadPaymentSession();
                break;
            case RETRY:
                showMessageAndPaymentSession(interaction, true);
                break;
            case TRY_OTHER_ACCOUNT:
            case TRY_OTHER_NETWORK:
                showMessageAndReloadPaymentSession(interaction, true);
                break;
            default:
                viewModel.setCloseWithCheckoutResult(result);
        }
    }

    private void handleDeleteAccountNetworkFailure() {
        viewModel.setShowConnectionErrorDialog(new PaymentDialogListener() {
            @Override
            public void onPositiveButtonClicked() {
                viewModel.deleteAccount();
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
                viewModel.loadPaymentSession();
                break;
            default:
                viewModel.setCloseWithCheckoutResult(result);
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
                viewModel.loadPaymentSession();
                break;
            case TRY_OTHER_ACCOUNT:
            case TRY_OTHER_NETWORK:
            case RETRY:
                showMessageAndReloadPaymentSession(interaction, false);
                break;
            default:
                viewModel.setCloseWithCheckoutResult(result);
        }
    }

    private void handleProcessPaymentResult(final CheckoutResult result) {
        if (result.isProceed()) {
            viewModel.setCloseWithCheckoutResult(result);
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
                viewModel.loadPaymentSession();
                break;
            case TRY_OTHER_ACCOUNT:
            case TRY_OTHER_NETWORK:
                showMessageAndReloadPaymentSession(interaction, false);
                break;
            case RETRY:
                showMessageAndPaymentSession(interaction, false);
                break;
            default:
                viewModel.setCloseWithCheckoutResult(result);
        }
    }

    private void handleProcessNetworkFailure(final CheckoutResult result) {
        viewModel.setShowConnectionErrorDialog(new PaymentDialogListener() {
            @Override
            public void onPositiveButtonClicked() {
                viewModel.processPayment();
            }

            @Override
            public void onNegativeButtonClicked() {
                viewModel.setCloseWithCheckoutResult(result);
            }

            @Override
            public void onDismissed() {
                viewModel.setCloseWithCheckoutResult(result);
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
                viewModel.loadPaymentSession();
            }

            @Override
            public void onNegativeButtonClicked() {
                viewModel.loadPaymentSession();
            }

            @Override
            public void onDismissed() {
                viewModel.loadPaymentSession();
            }
        };
        viewModel.setShowInteractionDialog(listener, message);
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
}