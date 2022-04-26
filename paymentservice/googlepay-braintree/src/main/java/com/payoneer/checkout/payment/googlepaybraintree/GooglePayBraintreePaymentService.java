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
import com.payoneer.checkout.redirect.RedirectService;
import com.payoneer.checkout.util.PaymentUtils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import androidx.fragment.app.Fragment;

/**
 * Create a new GooglePayBraintreePaymentService, this service handles payment requests
 * for GooglePay networks routed through Braintree provider.
 */
public class GooglePayBraintreePaymentService extends PaymentService {

    final static String BRAINTREE_AUTHORIZATION = "braintreeJsAuthorisation";
    final static String TAG = "GooglePayBraintree";

    private final static int IDLE = 0x00;
    private final static int DELETEACCOUNT_ACTIVE = 0x11;
    private final static int DELETEACCOUNT_REDIRECT = 0x12;
    private final static int PROCESSPAYMENT_ONSELECT = 0x20;
    private final static int PROCESSPAYMENT_GETTOKEN = 0x21;
    private final static int PROCESSPAYMENT_FINALIZE = 0x22;
    private final static int PROCESSPAYMENT_REDIRECT = 0x23;

    private final OperationService operationService;
    private Context applicationContext;
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
    public void processPayment(final RequestData requestData, final Context applicationContext) {
        resetPaymentService();
        this.requestData = requestData;
        this.applicationContext = applicationContext;
        this.state = PROCESSPAYMENT_ONSELECT;

        listener.onProcessPaymentActive();
        Operation operation = createOperation(requestData, PaymentLinkType.ONSELECT);
        operationService.postOperation(operation, applicationContext);
    }

    @Override
    public void deleteAccount(final RequestData requestData, final Context applicationContext) {
        resetPaymentService();
        this.requestData = requestData;
        this.applicationContext = applicationContext;
        this.state = DELETEACCOUNT_ACTIVE;

        listener.onDeleteAccountActive();
        DeleteAccount deleteAccount = createDeleteAccount(requestData);
        operationService.deleteAccount(deleteAccount, applicationContext);
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
        String braintreeAuthorization = PaymentUtils.getProviderParameterValue(BRAINTREE_AUTHORIZATION, operationResult);
        if (TextUtils.isEmpty((braintreeAuthorization))) {
            CheckoutResult checkoutResult = createFromErrorMessage("Braintree authorization key missing from OperationResult");
            closeWithProcessPaymentResult(requestData, checkoutResult);
            return;
        }
        Fragment fragment = GooglePayBraintreeFragment.newInstance(braintreeAuthorization);
        listener.showFragment(fragment);
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
            redirect(state, operationResult, applicationContext);
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
            redirect(state, operationResult, applicationContext);
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
        listener.onProcessPaymentResult(checkoutResult);
    }

    private void closeWithDeleteAccountResult(final RequestData requestData, final CheckoutResult checkoutResult) {
        resetPaymentService();
        Log.i(TAG, "closeWithDeleteAccountResult: " + checkoutResult);
        listener.onDeleteAccountResult(checkoutResult);
    }

    private CheckoutResult createFromErrorMessage(final String message) {
        String interactionCode = getErrorInteractionCode(requestData.getOperationType());
        return CheckoutResultHelper.fromErrorMessage(interactionCode, message);
    }

    private void resetPaymentService() {
        this.state = IDLE;
        this.requestData = null;
        this.applicationContext = null;
    }
}