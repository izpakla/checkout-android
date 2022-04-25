/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.payment.googlepaybraintree;

import static com.payoneer.checkout.model.InteractionCode.ABORT;
import static com.payoneer.checkout.model.InteractionCode.PROCEED;

import com.payoneer.checkout.CheckoutResult;
import com.payoneer.checkout.CheckoutResultHelper;
import com.payoneer.checkout.core.PaymentException;
import com.payoneer.checkout.core.PaymentLinkType;
import com.payoneer.checkout.model.Interaction;
import com.payoneer.checkout.model.OperationResult;
import com.payoneer.checkout.operation.DeleteAccount;
import com.payoneer.checkout.operation.Operation;
import com.payoneer.checkout.operation.OperationListener;
import com.payoneer.checkout.operation.OperationService;
import com.payoneer.checkout.payment.PaymentService;
import com.payoneer.checkout.payment.RequestData;
import com.payoneer.checkout.redirect.RedirectRequest;
import com.payoneer.checkout.redirect.RedirectService;
import com.payoneer.checkout.util.PaymentUtils;

import android.text.TextUtils;
import android.util.Log;
import androidx.fragment.app.Fragment;

/**
 * Create a new GooglePayBraintreePaymentService, this service handles payment requests
 * for GooglePay networks routed through Braintree provider.
 */
public class GooglePayBraintreePaymentService extends PaymentService {

    private final String TAG = "GooglePayBraintree";
    private final String BRAINTREE_AUTHORIZATION = "braintreeJsAuthorisation";

    private final int IDLE = 0x00;
    private final int DELETEACCOUNT_ACTIVE = 0x11;
    private final int DELETEACCOUNT_REDIRECT = 0x12;

    private final int PROCESSPAYMENT_ONSELECT = 0x20;
    private final int PROCESSPAYMENT_GETTOKEN = 0x21;
    private final int PROCESSPAYMENT_FINALIZE = 0x22;
    private final int PROCESSPAYMENT_REDIRECT = 0x23;

    private final OperationService operationService;
    private RequestData requestData;
    private int state;

    /**
     * Create a new BasicNetworkService, this service is a basic implementation
     * of the payment service that handles credit/debit cards and redirect networks.
     */
    public GooglePayBraintreePaymentService() {
        operationService = new OperationService();
        operationService.setListener(new OperationListener() {

            @Override
            public void onDeleteAccountSuccess(OperationResult operationResult) {
                handleDeleteAccountSuccess(operationResult);
            }

            @Override
            public void onDeleteAccountError(Throwable cause) {
                handleDeleteAccountError(cause);
            }

            @Override
            public void onOperationSuccess(OperationResult operationResult) {
                handleProcessPaymentSuccess(operationResult);
            }

            @Override
            public void onOperationError(Throwable cause) {
                handleProcessPaymentError(cause);
            }
        });
    }

    @Override
    public void onStop() {
        operationService.stop();
    }

    @Override
    public void resume() {
        if (isPending()) {
            handleRedirectResult();
        } else {
            throw new IllegalStateException("resume must not be called when PaymentService when isPending() returns false");
        }
    }

    @Override
    public boolean isPending() {
        return (state == PROCESSPAYMENT_REDIRECT) || (state == DELETEACCOUNT_REDIRECT);
    }

    @Override
    public void processPayment(final RequestData requestData) {
        resetPaymentService();
        this.requestData = requestData;
        this.state = PROCESSPAYMENT_ONSELECT;

        presenter.onProcessPaymentActive(requestData, false);
        Operation operation = createOperation(requestData, PaymentLinkType.ONSELECT);
        operationService.postOperation(operation, presenter.getApplicationContext());
    }

    @Override
    public void deleteAccount(final RequestData requestData) {
        resetPaymentService();
        this.requestData = requestData;
        this.state = DELETEACCOUNT_ACTIVE;

        presenter.onDeleteAccountActive(requestData);
        DeleteAccount deleteAccount = createDeleteAccount(requestData);
        operationService.deleteAccount(deleteAccount, presenter.getApplicationContext());
    }

    private void handleRedirectResult() {
        CheckoutResult checkoutResult;
        OperationResult operationResult = RedirectService.getRedirectResult();

        if (operationResult != null) {
            checkoutResult = new CheckoutResult(operationResult);
        } else {
            String message = "Missing OperationResult after client-side redirect";
            checkoutResult = createFromErrorMessage(message);
        }
        if (state == DELETEACCOUNT_REDIRECT) {
            closeWithDeleteAccountResult(requestData, checkoutResult);
        } else {
            closeWithProcessPaymentResult(requestData, checkoutResult);
        }
    }

    private void handleProcessPaymentSuccess(final OperationResult operationResult) {
        switch (state) {
            case PROCESSPAYMENT_ONSELECT:
                handleProcessOnSelectSuccess(operationResult);
                break;
            case PROCESSPAYMENT_FINALIZE:
                handleFinalizePaymentSuccess(operationResult);
        }
    }

    private void handleProcessOnSelectSuccess(final OperationResult operationResult) {
        state = PROCESSPAYMENT_GETTOKEN;
        String auth = PaymentUtils.getProviderParameterValue(BRAINTREE_AUTHORIZATION, operationResult);
        if (TextUtils.isEmpty((auth))) {
            CheckoutResult checkoutResult = createFromErrorMessage("Braintree authorization key missing from OperationResult");
            closeWithProcessPaymentResult(requestData, checkoutResult);
            return;
        }
        Fragment fragment = GooglePayBraintreeFragment.newInstance();
        presenter.showCustomFragment(fragment);
    }

    private void handleFinalizePaymentSuccess(final OperationResult operationResult) {
        Interaction interaction = operationResult.getInteraction();
        CheckoutResult checkoutResult = new CheckoutResult(operationResult);

        if (!(PROCEED.equals(interaction.getCode()) || requiresRedirect(operationResult))) {
            closeWithProcessPaymentResult(requestData, checkoutResult);
            return;
        }
        try {
            state = PROCESSPAYMENT_REDIRECT;
            redirect(state, operationResult);
        } catch (PaymentException e) {
            handleProcessPaymentError(e);
        }
    }

    private void handleProcessPaymentError(Throwable cause) {
        String code = getErrorInteractionCode(requestData.getOperationType());
        CheckoutResult checkoutResult = CheckoutResultHelper.fromThrowable(code, cause);
        closeWithProcessPaymentResult(requestData, checkoutResult);
    }

    private void handleDeleteAccountSuccess(OperationResult operationResult) {
        Interaction interaction = operationResult.getInteraction();
        CheckoutResult checkoutResult = new CheckoutResult(operationResult);

        if (!(PROCEED.equals(interaction.getCode()) || requiresRedirect(operationResult))) {
            closeWithDeleteAccountResult(requestData, checkoutResult);
            return;
        }
        try {
            state = DELETEACCOUNT_REDIRECT;
            redirect(state, operationResult);
        } catch (PaymentException e) {
            handleDeleteAccountError(e);
        }
    }

    private void handleDeleteAccountError(final Throwable cause) {
        CheckoutResult checkoutResult = CheckoutResultHelper.fromThrowable(ABORT, cause);
        closeWithDeleteAccountResult(requestData, checkoutResult);
    }

    private void closeWithProcessPaymentResult(final RequestData requestData, final CheckoutResult checkoutResult) {
        resetPaymentService();
        Log.i(TAG, "closeWithProcessPaymentResult: " + checkoutResult);
        presenter.onProcessPaymentResult(requestData, checkoutResult);
    }

    private void closeWithDeleteAccountResult(final RequestData requestData, final CheckoutResult checkoutResult) {
        resetPaymentService();
        Log.i(TAG, "closeWithDeleteAccountResult: " + checkoutResult);
        presenter.onDeleteAccountResult(requestData, checkoutResult);
    }

    private CheckoutResult createFromErrorMessage(final String message) {
        String interactionCode = getErrorInteractionCode(requestData.getOperationType());
        return CheckoutResultHelper.fromErrorMessage(interactionCode, message);
    }

    private void resetPaymentService() {
        this.state = IDLE;
        this.requestData = null;
    }
}