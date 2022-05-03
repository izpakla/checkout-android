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
import com.payoneer.checkout.payment.ProcessPaymentData;
import com.payoneer.checkout.redirect.RedirectService;
import com.payoneer.checkout.util.NetworkLogoLoader;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

/**
 * BasicNetworkService implementing the handling of basic payment methods like Visa, Mastercard and Sepa.
 * This network service also supports redirect networks like Paypal.
 */
public final class BasicPaymentService extends PaymentService {

    private final static String TAG = "BasicPaymentService";
    private final static int IDLE = 0x00;
    private final static int PROCESS = 0x01;
    private final static int REDIRECT = 0x02;

    private final OperationService operationService;
    private ProcessPaymentData processPaymentData;
    private Context applicationContext;
    private int state;

    /**
     * Create a new BasicNetworkService, this service is a basic implementation
     * of the payment service that handles credit/debit cards and redirect networks.
     */
    private BasicPaymentService() {
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

    /**
     * Get the instance of this BasicPaymentService
     *
     * @return the instance of this BasicPaymentService
     */
    public static BasicPaymentService getInstance() {
        return BasicPaymentService.InstanceHolder.INSTANCE;
    }

    @Override
    public void stop() {
        operationService.stop();
    }

    @Override
    public void reset() {
        this.state = IDLE;
        this.applicationContext = null;
        this.processPaymentData = null;
        operationService.stop();
    }

    @Override
    public boolean resume() {
        if (state == REDIRECT) {
            handleRedirectResult();
            return true;
        }
        return false;
    }

    @Override
    public void onFragmentResult(final Bundle fragmentResult) {
    }

    public boolean isActive() {
        return (state != IDLE);
    }

    @Override
    public void processPayment(final ProcessPaymentData processPaymentData, final Context applicationContext) {
        this.state = PROCESS;
        this.applicationContext = applicationContext;
        this.processPaymentData = processPaymentData;

        notifyOnProcessPaymentActive(true);
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
            this.state = REDIRECT;
            redirect(0, operationResult, applicationContext);
        } catch (PaymentException e) {
            handleProcessPaymentError(e);
        }
    }

    private void handleProcessPaymentError(Throwable cause) {
        String code = getErrorInteractionCode(processPaymentData.getOperationType());
        CheckoutResult checkoutResult = CheckoutResultHelper.fromThrowable(code, cause);
        closeWithProcessPaymentResult(checkoutResult);
    }

    private void closeWithProcessPaymentResult(final CheckoutResult checkoutResult) {
        reset();
        Log.i(TAG, "closeWithProcessPaymentResult: " + checkoutResult);
        notifyOnProcessPaymentResult(checkoutResult);
    }

    private static class InstanceHolder {
        static final BasicPaymentService INSTANCE = new BasicPaymentService();
    }
}
