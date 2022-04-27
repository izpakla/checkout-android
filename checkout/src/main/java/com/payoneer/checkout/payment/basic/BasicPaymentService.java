/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.payment.basic;

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

import android.content.Context;
import android.util.Log;

/**
 * BasicNetworkService implementing the handling of basic payment methods like Visa, Mastercard and Sepa.
 * This network service also supports redirect networks like Paypal.
 */
public final class BasicPaymentService extends PaymentService {

    private final static String TAG = "BasicPaymentService";
    private final static int PROCESSPAYMENT_REQUEST_CODE = 0;
    private final static int DELETEACCOUNT_REQUEST_CODE = 1;

    private final OperationService operationService;
    private RequestData requestData;
    private Context applicationContext;
    private RedirectRequest redirectRequest;

    /**
     * Create a new BasicNetworkService, this service is a basic implementation
     * of the payment service that handles credit/debit cards and redirect networks.
     */
    public BasicPaymentService() {
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
        if (redirectRequest == null) {
            return false;
        }
        handleRedirectResult(redirectRequest);
        redirectRequest = null;
        return true;
    }

    @Override
    public void processPayment(final RequestData requestData, final Context applicationContext) {
        resetPaymentService();
        this.applicationContext = applicationContext;
        this.requestData = requestData;

        listener.onProcessPaymentActive();
        Operation operation = createOperation(requestData, PaymentLinkType.OPERATION);
        operationService.postOperation(operation, applicationContext);
    }

    @Override
    public void deleteAccount(final RequestData requestData, final Context applicationContext) {
        resetPaymentService();
        this.applicationContext = applicationContext;
        this.requestData = requestData;

        listener.onDeleteAccountActive();
        DeleteAccount deleteAccount = createDeleteAccount(requestData);
        operationService.deleteAccount(deleteAccount, applicationContext);
    }

    private void handleRedirectResult(RedirectRequest request) {
        CheckoutResult checkoutResult;
        OperationResult operationResult = RedirectService.getRedirectResult();

        if (operationResult != null) {
            checkoutResult = new CheckoutResult(operationResult);
        } else {
            String message = "Missing OperationResult after client-side redirect";
            String interactionCode = getErrorInteractionCode(requestData.getOperationType());
            checkoutResult = CheckoutResultHelper.fromErrorMessage(interactionCode, message);
        }
        if (request.getRequestCode() == PROCESSPAYMENT_REQUEST_CODE) {
            closeWithProcessPaymentResult(checkoutResult);
        } else {
            closeWithDeleteAccountResult(checkoutResult);
        }
    }

    private void handleProcessPaymentSuccess(final OperationResult operationResult) {
        Interaction interaction = operationResult.getInteraction();
        CheckoutResult checkoutResult = new CheckoutResult(operationResult);

        if (!(PROCEED.equals(interaction.getCode()) && requiresRedirect(operationResult))) {
            closeWithProcessPaymentResult(checkoutResult);
            return;
        }
        try {
            redirectRequest = redirect(applicationContext, PROCESSPAYMENT_REQUEST_CODE, operationResult);
        } catch (PaymentException e) {
            handleProcessPaymentError(e);
        }
    }

    private void handleProcessPaymentError(Throwable cause) {
        String code = getErrorInteractionCode(requestData.getOperationType());
        CheckoutResult checkoutResult = CheckoutResultHelper.fromThrowable(code, cause);
        closeWithProcessPaymentResult(checkoutResult);
    }

    private void handleDeleteAccountSuccess(final OperationResult operationResult) {
        Interaction interaction = operationResult.getInteraction();
        CheckoutResult checkoutResult = new CheckoutResult(operationResult);

        if (!(PROCEED.equals(interaction.getCode()) && requiresRedirect(operationResult))) {
            closeWithDeleteAccountResult(checkoutResult);
            return;
        }
        try {
            redirectRequest = redirect(applicationContext, DELETEACCOUNT_REQUEST_CODE, operationResult);
        } catch (PaymentException e) {
            handleDeleteAccountError(e);
        }
    }

    private void handleDeleteAccountError(Throwable cause) {
        CheckoutResult checkoutResult = CheckoutResultHelper.fromThrowable(ABORT, cause);
        closeWithDeleteAccountResult(checkoutResult);
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

    private void resetPaymentService() {
        this.applicationContext = null;
        this.redirectRequest = null;
        this.requestData = null;
    }
}
