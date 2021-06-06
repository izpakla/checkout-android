/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.service.basic;

import static com.payoneer.checkout.model.InteractionCode.ABORT;
import static com.payoneer.checkout.model.InteractionCode.PROCEED;
import static com.payoneer.checkout.model.InteractionCode.VERIFY;
import static com.payoneer.checkout.model.NetworkOperationType.ACTIVATION;
import static com.payoneer.checkout.model.NetworkOperationType.CHARGE;
import static com.payoneer.checkout.model.NetworkOperationType.PAYOUT;
import static com.payoneer.checkout.model.NetworkOperationType.PRESET;
import static com.payoneer.checkout.model.NetworkOperationType.UPDATE;
import static com.payoneer.checkout.model.RedirectType.HANDLER3DS2;
import static com.payoneer.checkout.model.RedirectType.PROVIDER;
import static com.payoneer.checkout.ui.PaymentActivityResult.RESULT_CODE_ERROR;
import static com.payoneer.checkout.ui.PaymentActivityResult.RESULT_CODE_PROCEED;

import com.payoneer.checkout.core.PaymentException;
import com.payoneer.checkout.form.DeleteAccount;
import com.payoneer.checkout.form.Operation;
import com.payoneer.checkout.model.AccountRegistration;
import com.payoneer.checkout.model.Interaction;
import com.payoneer.checkout.model.OperationResult;
import com.payoneer.checkout.model.Redirect;
import com.payoneer.checkout.model.RedirectType;
import com.payoneer.checkout.redirect.RedirectRequest;
import com.payoneer.checkout.ui.PaymentResult;
import com.payoneer.checkout.ui.service.NetworkService;
import com.payoneer.checkout.ui.service.OperationListener;
import com.payoneer.checkout.ui.service.OperationService;
import com.payoneer.checkout.util.PaymentResultHelper;

import android.content.Context;
import android.util.Log;

/**
 * BasicNetworkService implementing the handling of basic payment methods like Visa, Mastercard and Sepa.
 * This network service also supports redirect networks like Paypal.
 */
public final class BasicNetworkService extends NetworkService {

    private final static int PROCESS_PAYMENT = 0;    
    private final static int DELETE_ACCOUNT = 1;
    
    private final OperationService operationService;
    private String operationType;
    
    /**
     * Create a new BasicNetworkService, this service is a basic implementation
     * that sends an operation to the Payment API.
     *
     * @param context context in which this network service will operate
     */
    public BasicNetworkService(Context context) {
        operationService = new OperationService(context);
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
    public void processPayment(Operation operation) {
        this.operationType = operation.getOperationType();
        listener.showProgress(true);
        operationService.postOperation(operation);
    }

    @Override
    public void deleteAccount(DeleteAccount account) {
        this.operationType = UPDATE;
        listener.showProgress(true);
        operationService.deleteAccount(account);
    }

    @Override
    public void onRedirectResult(RedirectRequest request, OperationResult operationResult) {
        int resultCode;
        PaymentResult paymentResult;
        
        if (operationResult != null) {
            Interaction interaction = operationResult.getInteraction();
            resultCode = PROCEED.equals(interaction.getCode()) ? RESULT_CODE_PROCEED : RESULT_CODE_ERROR;
            paymentResult = new PaymentResult(operationResult);
        } else {
            String message = "Missing OperationResult after client-side redirect";
            String interactionCode = getErrorInteractionCode(operationType);
            resultCode = RESULT_CODE_ERROR;
            paymentResult = PaymentResultHelper.fromErrorMessage(interactionCode, message);
        }
        if (request.getRequestType() == PROCESS_PAYMENT) {
            listener.onProcessPaymentResult(resultCode, paymentResult);
        } else {
            listener.onDeleteAccountResult(resultCode, paymentResult);
        }
    }

    private void handleProcessPaymentSuccess(OperationResult operationResult) {
        Interaction interaction = operationResult.getInteraction();
        PaymentResult result = new PaymentResult(operationResult);

        if (!PROCEED.equals(interaction.getCode())) {
            listener.onProcessPaymentResult(RESULT_CODE_ERROR, result);
            return;
        }
        if (requiresRedirect(operationResult)) {
            try {
                RedirectRequest request = RedirectRequest.fromOperationResult(PROCESS_PAYMENT, operationResult);
                listener.redirect(request);
            } catch (PaymentException e) {
                handleProcessPaymentError(e);
            }
            return;
        }
        listener.onProcessPaymentResult(RESULT_CODE_PROCEED, result);
    }

    private void handleProcessPaymentError(Throwable cause) {
        String code = getErrorInteractionCode(operationType);
        PaymentResult paymentResult = PaymentResultHelper.fromThrowable(code, cause);
        listener.onProcessPaymentResult(RESULT_CODE_ERROR, paymentResult);
    }
    
    private void handleDeleteAccountSuccess(OperationResult operationResult) {
        Interaction interaction = operationResult.getInteraction();
        PaymentResult result = new PaymentResult(operationResult);

        if (!PROCEED.equals(interaction.getCode())) {
            listener.onDeleteAccountResult(RESULT_CODE_ERROR, result);
            return;
        }
        if (requiresRedirect(operationResult)) {
            try {
                RedirectRequest request = RedirectRequest.fromOperationResult(DELETE_ACCOUNT, operationResult);
                listener.redirect(request);
            } catch (PaymentException e) {
                handleDeleteAccountError(e);
            }
            return;
        }
        listener.onDeleteAccountResult(RESULT_CODE_PROCEED, result);
    }

    private void handleDeleteAccountError(Throwable cause) {
        PaymentResult paymentResult = PaymentResultHelper.fromThrowable(ABORT, cause);
        listener.onDeleteAccountResult(RESULT_CODE_ERROR, paymentResult);
    }

    private boolean requiresRedirect(OperationResult operationResult) {
        Redirect redirect = operationResult.getRedirect();
        String type = redirect != null ? redirect.getType() : null;
        return PROVIDER.equals(redirect.getType()) || HANDLER3DS2.equals(type);
    }

    private String getErrorInteractionCode(String operationType) {
        return CHARGE.equals(operationType) || PAYOUT.equals(operationType) ? VERIFY : ABORT;
    }
}
