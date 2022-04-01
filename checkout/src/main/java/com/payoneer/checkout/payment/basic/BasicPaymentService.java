/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.payment.basic;

import static com.payoneer.checkout.CheckoutActivityResult.RESULT_CODE_ERROR;
import static com.payoneer.checkout.CheckoutActivityResult.RESULT_CODE_PROCEED;
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

    private final static int PROCESSPAYMENT_REQUEST_CODE = 0;
    private final static int DELETEACCOUNT_REQUEST_CODE = 1;

    private final OperationService operationService;
    private RequestData requestData;
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
    public void resume() {
        if (redirectRequest != null) {
            redirectRequest = null;
            handleRedirectResult(redirectRequest);
        }
    }

    @Override
    public boolean isPaused() {
        return redirectRequest != null;
    }

    @Override
    public void processPayment(final RequestData requestData, final Context context) {
        this.requestData = requestData;
        this.redirectRequest = null;

        controller.onProgress(true);
        Operation operation = createOperation(requestData, PaymentLinkType.OPERATION);
        operationService.postOperation(operation, context);
    }

    @Override
    public void deleteAccount(final RequestData requestData, final Context context) {
        this.requestData = requestData;
        this.redirectRequest = null;

        controller.onProgress(true);
        DeleteAccount deleteAccount = createDeleteAccount(requestData);
        operationService.deleteAccount(deleteAccount, context);
    }

    private void handleRedirectResult(RedirectRequest request) {
        int resultCode;
        CheckoutResult checkoutResult;

        controller.onProgress(true);
        OperationResult operationResult = RedirectService.getRedirectResult();

        if (operationResult != null) {
            Interaction interaction = operationResult.getInteraction();
            resultCode = PROCEED.equals(interaction.getCode()) ? RESULT_CODE_PROCEED : RESULT_CODE_ERROR;
            checkoutResult = new CheckoutResult(operationResult);
        } else {
            String message = "Missing OperationResult after client-side redirect";
            String interactionCode = getErrorInteractionCode(requestData.getOperationType());
            resultCode = RESULT_CODE_ERROR;
            checkoutResult = CheckoutResultHelper.fromErrorMessage(interactionCode, message);
        }
        Log.i("checkout-sdk", "onRedirectResult: " + checkoutResult);

        if (request.getRequestCode() == PROCESSPAYMENT_REQUEST_CODE) {
            controller.onProcessPaymentResult(resultCode, checkoutResult);
        } else {
            controller.onDeleteAccountResult(resultCode, checkoutResult);
        }
    }

    private void handleProcessPaymentSuccess(OperationResult operationResult) {
        Interaction interaction = operationResult.getInteraction();
        CheckoutResult checkoutResult = new CheckoutResult(operationResult);
        Log.i("checkout-sdk", "handleProcessPaymentSuccess: " + checkoutResult);

        if (!PROCEED.equals(interaction.getCode())) {
            controller.onProcessPaymentResult(RESULT_CODE_ERROR, checkoutResult);
            return;
        }
        if (!requiresRedirect(operationResult)) {
            controller.onProcessPaymentResult(RESULT_CODE_PROCEED, checkoutResult);
            return;
        }
        try {
            redirectRequest = redirect(PROCESSPAYMENT_REQUEST_CODE, operationResult);
        } catch (PaymentException e) {
            handleProcessPaymentError(e);
        }
    }

    private void handleProcessPaymentError(Throwable cause) {
        String code = getErrorInteractionCode(requestData.getOperationType());
        CheckoutResult checkoutResult = CheckoutResultHelper.fromThrowable(code, cause);
        Log.i("checkout-sdk", "handleProcessPaymentError: " + checkoutResult);
        controller.onProcessPaymentResult(RESULT_CODE_ERROR, checkoutResult);
    }

    private void handleDeleteAccountSuccess(OperationResult operationResult) {
        Interaction interaction = operationResult.getInteraction();
        CheckoutResult checkoutResult = new CheckoutResult(operationResult);
        Log.i("checkout-sdk", "handleDeleteAccountSuccess: " + checkoutResult);

        if (!PROCEED.equals(interaction.getCode())) {
            controller.onDeleteAccountResult(RESULT_CODE_ERROR, checkoutResult);
            return;
        }
        if (!requiresRedirect(operationResult)) {
            controller.onDeleteAccountResult(RESULT_CODE_PROCEED, checkoutResult);
            return;
        }
        try {
            redirectRequest = redirect(DELETEACCOUNT_REQUEST_CODE, operationResult);
        } catch (PaymentException e) {
            handleDeleteAccountError(e);
        }
    }

    private void handleDeleteAccountError(Throwable cause) {
        CheckoutResult checkoutResult = CheckoutResultHelper.fromThrowable(ABORT, cause);
        Log.i("checkout-sdk", "handleDeleteAccountError: " + checkoutResult);
        controller.onDeleteAccountResult(RESULT_CODE_ERROR, checkoutResult);
    }
}
