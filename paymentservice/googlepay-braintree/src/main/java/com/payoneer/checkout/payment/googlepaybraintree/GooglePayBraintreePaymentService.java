/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.payment.googlepaybraintree;

import static com.payoneer.checkout.model.InteractionCode.PROCEED;

import com.braintreepayments.api.GooglePayRequest;
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
    private final static int ONSELECT = 0x01;
    private final static int GETTOKEN = 0x02;
    private final static int FINALIZE = 0x03;
    private final static int REDIRECT = 0x04;

    private final OperationService operationService;
    private Context applicationContext;
    private ProcessPaymentData processPaymentData;
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
        switch (state) {
            case REDIRECT:
                handleRedirectResult();
                return true;
            case GETTOKEN:
                handleGetTokenResult();
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean isActive() {
        return (state != IDLE);
    }

    @Override
    public void onFragmentResult(final Bundle fragmentResult) {
        this.fragmentResult = fragmentResult;
    }

    @Override
    public void processPayment(final ProcessPaymentData processPaymentData, final Context applicationContext) {
        this.state = ONSELECT;
        this.processPaymentData = processPaymentData;
        this.applicationContext = applicationContext;
        this.fragmentResult = null;

        notifyOnProcessPaymentActive();
        Operation operation = createOperation(processPaymentData, PaymentLinkType.ONSELECT);
        operationService.postOperation(operation, applicationContext);
    }

    private void handleGetTokenResult() {
        notifyOnProcessPaymentActive();
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
        closeWithProcessPaymentResult(checkoutResult);
    }

    private void handleProcessPaymentSuccess(final OperationResult operationResult) {
        switch (state) {
            case ONSELECT:
                handleProcessOnSelectSuccess(operationResult);
                break;
            case FINALIZE:
                handleFinalizePaymentSuccess(operationResult);
        }
    }

    private void handleProcessOnSelectSuccess(final OperationResult operationResult) {
        state = GETTOKEN;
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
            notifyShowFragment(GooglePayBraintreeFragment.newInstance(arguments));
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
            state = REDIRECT;
            redirect(state, operationResult, applicationContext);
        } catch (PaymentException e) {
            handleProcessPaymentError(e);
        }
    }

    private void handleProcessPaymentError(Throwable cause) {
        String code = getErrorInteractionCode(processPaymentData.getOperationType());
        closeWithProcessPaymentResult(CheckoutResultHelper.fromThrowable(code, cause));
    }

    private void closeWithProcessErrorMessage(final String message) {
        CheckoutResult result = createFromErrorMessage(message);
        closeWithProcessPaymentResult(result);
    }

    private CheckoutResult createFromErrorMessage(final String message) {
        String interactionCode = getErrorInteractionCode(processPaymentData.getOperationType());
        return CheckoutResultHelper.fromErrorMessage(interactionCode, message);
    }

    private void closeWithProcessPaymentResult(final CheckoutResult checkoutResult) {
        this.state = IDLE;
        this.processPaymentData = null;
        this.applicationContext = null;
        this.fragmentResult = null;

        Log.i(TAG, "closeWithProcessPaymentResult: " + checkoutResult);
        notifyOnProcessPaymentResult(checkoutResult);
    }
}
