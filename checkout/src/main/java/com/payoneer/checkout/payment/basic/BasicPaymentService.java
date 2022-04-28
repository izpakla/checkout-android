/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.payment.basic;

import static com.payoneer.checkout.model.InteractionCode.PROCEED;

import com.payoneer.checkout.CheckoutResult;
import com.payoneer.checkout.CheckoutResultHelper;
import com.payoneer.checkout.core.PaymentException;
import com.payoneer.checkout.core.PaymentLinkType;
import com.payoneer.checkout.model.Interaction;
import com.payoneer.checkout.model.OperationResult;
import com.payoneer.checkout.operation.Operation;
import com.payoneer.checkout.operation.OperationListener;
import com.payoneer.checkout.operation.OperationService;
import com.payoneer.checkout.payment.PaymentService;
import com.payoneer.checkout.payment.processPaymentData;
import com.payoneer.checkout.redirect.RedirectRequest;
import com.payoneer.checkout.redirect.RedirectService;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

/**
 * BasicNetworkService implementing the handling of basic payment methods like Visa, Mastercard and Sepa.
 * This network service also supports redirect networks like Paypal.
 */
public final class BasicPaymentService extends PaymentService {

    private final static String TAG = "BasicPaymentService";
    private final OperationService operationService;
    private processPaymentData processPaymentData;
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
    public boolean resume() {
        if (redirectRequest != null) {
            handleRedirectResult();
            return true;
        }
        return false;
    }

    @Override
    public void onFragmentResult(final Bundle fragmentResult) {
    }

    public boolean isActive() {
        return operationService.isActive();
    }

    @Override
    public void processPayment(final processPaymentData processPaymentData, final Context applicationContext) {
        this.applicationContext = applicationContext;
        this.processPaymentData = processPaymentData;
        this.redirectRequest = null;

        notifyProcessPaymentActive();
        Operation operation = createOperation(processPaymentData, PaymentLinkType.OPERATION);
        operationService.postOperation(operation, applicationContext);
    }

    private void handleRedirectResult() {
        CheckoutResult checkoutResult;
        OperationResult operationResult = RedirectService.getRedirectResult();

        if (operationResult != null) {
            checkoutResult = new CheckoutResult(operationResult);
        } else {
            String message = "Missing OperationResult after client-side redirect";
            String interactionCode = getErrorInteractionCode(processPaymentData.getOperationType());
            checkoutResult = CheckoutResultHelper.fromErrorMessage(interactionCode, message);
        }
        closeWithProcessPaymentResult(checkoutResult);
    }

    private void handleProcessPaymentSuccess(final OperationResult operationResult) {
        Interaction interaction = operationResult.getInteraction();
        CheckoutResult checkoutResult = new CheckoutResult(operationResult);

        if (!(PROCEED.equals(interaction.getCode()) && requiresRedirect(operationResult))) {
            closeWithProcessPaymentResult(checkoutResult);
            return;
        }
        try {
            redirectRequest = redirect(0, operationResult, applicationContext);
        } catch (PaymentException e) {
            handleProcessPaymentError(e);
        }
    }

    private void handleProcessPaymentError(Throwable cause) {
        String code = getErrorInteractionCode(processPaymentData.getOperationType());
        CheckoutResult checkoutResult = CheckoutResultHelper.fromThrowable(code, cause);
        closeWithProcessPaymentResult(checkoutResult);
    }

    private void notifyProcessPaymentActive() {
            if (listener != null) {
                listener.onProcessPaymentActive();
            }
        }

    private void closeWithProcessPaymentResult(final CheckoutResult checkoutResult) {
        Log.i(TAG, "closeWithProcessPaymentResult: " + checkoutResult);
        if (listener != null) {
            listener.onProcessPaymentResult(checkoutResult);
        }
        this.applicationContext = null;
        this.redirectRequest = null;
        this.processPaymentData = null;
    }
}
