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
import static com.payoneer.checkout.model.InteractionCode.VERIFY;
import static com.payoneer.checkout.model.NetworkOperationType.CHARGE;
import static com.payoneer.checkout.model.NetworkOperationType.PAYOUT;
import static com.payoneer.checkout.model.RedirectType.HANDLER3DS2;
import static com.payoneer.checkout.model.RedirectType.PROVIDER;

import com.payoneer.checkout.CheckoutResult;
import com.payoneer.checkout.CheckoutResultHelper;
import com.payoneer.checkout.core.PaymentException;
import com.payoneer.checkout.core.PaymentLinkType;
import com.payoneer.checkout.model.Interaction;
import com.payoneer.checkout.model.OperationResult;
import com.payoneer.checkout.model.Redirect;
import com.payoneer.checkout.operation.DeleteAccount;
import com.payoneer.checkout.operation.Operation;
import com.payoneer.checkout.operation.OperationListener;
import com.payoneer.checkout.operation.OperationService;
import com.payoneer.checkout.payment.PaymentRequest;
import com.payoneer.checkout.payment.PaymentService;
import com.payoneer.checkout.redirect.RedirectRequest;

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
    private String operationType;

    /**
     * Create a new BasicNetworkService, this service is a basic implementation
     * that sends an operation to the Payment API.
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
    public void stop() {
        operationService.stop();
    }

    @Override
    public boolean isProcessing() {
        return false;
    }

    @Override
    public void resumeProcessing() {
    }

    @Override
    public void processPayment(final PaymentRequest paymentRequest, Context context) {
        this.operationType = paymentRequest.getOperationType();

        controller.showProgress(true);
        Operation operation = paymentRequest.createOperationWithLink(PaymentLinkType.OPERATION);
        operationService.postOperation(operation, context);
    }

    @Override
    public void deleteAccount(DeleteAccount account, Context context) {
        this.operationType = account.getOperationType();
        controller.showProgress(true);
        operationService.deleteAccount(account, context);
    }

    /*
    @Override
    public void onRedirectResult(RedirectRequest request, OperationResult operationResult) {
        int resultCode;
        CheckoutResult checkoutResult;

        if (operationResult != null) {
            Interaction interaction = operationResult.getInteraction();
            resultCode = PROCEED.equals(interaction.getCode()) ? RESULT_CODE_PROCEED : RESULT_CODE_ERROR;
            checkoutResult = new CheckoutResult(operationResult);
        } else {
            String message = "Missing OperationResult after client-side redirect";
            String interactionCode = getErrorInteractionCode(operationType);
            resultCode = RESULT_CODE_ERROR;
            checkoutResult = CheckoutResultHelper.fromErrorMessage(interactionCode, message);
        }
        Log.i("checkout-sdk", "onRedirectResult: " + checkoutResult);

        if (request.getRequestCode() == PROCESSPAYMENT_REQUEST_CODE) {
            listener.onProcessPaymentResult(resultCode, checkoutResult);
        } else {
            listener.onDeleteAccountResult(resultCode, checkoutResult);
        }
    }*/

    private void handleProcessPaymentSuccess(OperationResult operationResult) {
        Interaction interaction = operationResult.getInteraction();
        CheckoutResult checkoutResult = new CheckoutResult(operationResult);
        Log.i("checkout-sdk", "handleProcessPaymentSuccess: " + checkoutResult);

        if (!PROCEED.equals(interaction.getCode())) {
            controller.onProcessPaymentResult(RESULT_CODE_ERROR, checkoutResult);
            return;
        }
        /**
        if (requiresRedirect(operationResult)) {
            try {
                RedirectRequest request = RedirectRequest.fromOperationResult(PROCESSPAYMENT_REQUEST_CODE, operationResult);
                controller.redirect(request);
            } catch (PaymentException e) {
                handleProcessPaymentError(e);
            }
            return;
        }*/
        controller.onProcessPaymentResult(RESULT_CODE_PROCEED, checkoutResult);
    }

    private void handleProcessPaymentError(Throwable cause) {
        String code = getErrorInteractionCode(operationType);
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
        /*
        if (requiresRedirect(operationResult)) {
            try {
                RedirectRequest request = RedirectRequest.fromOperationResult(DELETEACCOUNT_REQUEST_CODE, operationResult);
                controller.redirect(request);
            } catch (PaymentException e) {
                handleDeleteAccountError(e);
            }
            return;
        }*/
        controller.onDeleteAccountResult(RESULT_CODE_PROCEED, checkoutResult);
    }

    private void handleDeleteAccountError(Throwable cause) {
        CheckoutResult checkoutResult = CheckoutResultHelper.fromThrowable(ABORT, cause);
        Log.i("checkout-sdk", "handleDeleteAccountError: " + checkoutResult);
        controller.onDeleteAccountResult(RESULT_CODE_ERROR, checkoutResult);
    }

    private boolean requiresRedirect(OperationResult operationResult) {
        Redirect redirect = operationResult.getRedirect();
        String type = redirect != null ? redirect.getType() : null;
        return PROVIDER.equals(type) || HANDLER3DS2.equals(type);
    }

    private String getErrorInteractionCode(String operationType) {
        return CHARGE.equals(operationType) || PAYOUT.equals(operationType) ? VERIFY : ABORT;
    }
}
