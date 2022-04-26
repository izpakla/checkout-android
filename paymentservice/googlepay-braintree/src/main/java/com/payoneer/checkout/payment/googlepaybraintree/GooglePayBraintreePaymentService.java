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

import com.braintreepayments.api.GooglePayRequest;
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
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

/**
 * Create a new GooglePayBraintreePaymentService, this service handles payment requests
 * for GooglePay networks routed through Braintree provider.
 */
public class GooglePayBraintreePaymentService extends PaymentService {

    final static String TAG = "GooglePayBraintree";
    final static String GOOGLEPAY_REQUEST = "googlepayrequest";
    final static String BRAINTREE_AUTHORIZATION = "braintreeJsAuthorisation";

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
    private Bundle fragmentResult;
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
    public boolean onResume() {
        switch (state) {
            case PROCESSPAYMENT_REDIRECT:
            case DELETEACCOUNT_REDIRECT:
                handleRedirectResult();
                return true;
            case PROCESSPAYMENT_GETTOKEN:
                handleGetTokenResult();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onFragmentResult(final Bundle fragmentResult) {
        this.fragmentResult = fragmentResult;
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

    private void handleGetTokenResult() {
        listener.onProcessPaymentActive();
        if (fragmentResult == null) {
            closeWithProcessErrorMessage("Missing GooglePayBraintree fragment result after onResume");
            return;
        }
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
            closeWithDeleteAccountResult(checkoutResult);
        } else {
            closeWithProcessPaymentResult(checkoutResult);
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
            closeWithProcessErrorMessage("Missing GooglePayBraintree [" + BRAINTREE_AUTHORIZATION + "] parameter");
            return;
        }
        try {
            GooglePayRequest request = GooglePayRequestBuilder.of(operationResult);
            Bundle arguments = new Bundle();
            arguments.putString(BRAINTREE_AUTHORIZATION, braintreeAuthorization);
            arguments.putParcelable(GOOGLEPAY_REQUEST, request);
            listener.showFragment(GooglePayBraintreeFragment.newInstance(arguments));
        } catch (PaymentException e) {
            handleProcessPaymentError(e);
        }
    }

    private void handleFinalizePaymentSuccess(final OperationResult operationResult) {
        Interaction interaction = operationResult.getInteraction();
        CheckoutResult checkoutResult = new CheckoutResult(operationResult);

        if (!(PROCEED.equals(interaction.getCode()) || requiresRedirect(operationResult))) {
            closeWithProcessPaymentResult(checkoutResult);
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
        closeWithProcessPaymentResult(CheckoutResultHelper.fromThrowable(code, cause));
    }

    private void handleDeleteAccountSuccess(OperationResult operationResult) {
        Interaction interaction = operationResult.getInteraction();
        CheckoutResult checkoutResult = new CheckoutResult(operationResult);

        if (!(PROCEED.equals(interaction.getCode()) || requiresRedirect(operationResult))) {
            closeWithDeleteAccountResult(checkoutResult);
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
        closeWithDeleteAccountResult(checkoutResult);
    }

    private void closeWithProcessErrorMessage(final String message) {
        CheckoutResult result = createFromErrorMessage(message);
        closeWithProcessPaymentResult(result);
    }

    private void closeWithProcessPaymentResult(final CheckoutResult checkoutResult) {
        resetPaymentService();
        Log.i(TAG, "closeWithProcessPaymentResult: " + checkoutResult);
        listener.onProcessPaymentResult(checkoutResult);
    }

    private void closeWithDeleteAccountResult(final CheckoutResult checkoutResult) {
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
        this.fragmentResult = null;
    }
}