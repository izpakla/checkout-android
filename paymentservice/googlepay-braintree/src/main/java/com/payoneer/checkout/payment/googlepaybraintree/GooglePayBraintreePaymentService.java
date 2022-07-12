/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.payment.googlepaybraintree;

import static com.payoneer.checkout.model.InteractionCode.PROCEED;
import static com.payoneer.checkout.model.NetworkOperationType.PRESET;

import java.util.ArrayList;
import java.util.List;

import com.braintreepayments.api.GooglePayRequest;
import com.braintreepayments.api.PaymentMethodNonce;
import com.payoneer.checkout.CheckoutResult;
import com.payoneer.checkout.CheckoutResultHelper;
import com.payoneer.checkout.core.PaymentException;
import com.payoneer.checkout.core.PaymentLinkType;
import com.payoneer.checkout.model.Interaction;
import com.payoneer.checkout.model.OperationResult;
import com.payoneer.checkout.model.Parameter;
import com.payoneer.checkout.model.ProviderParameters;
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
    final static String GOOGLEPAY_REQUEST = "googlepayRequest";
    final static String BRAINTREE_NONCE = "braintreeNonce";
    final static String BRAINTREE_ERROR = "braintreeError";
    final static String BRAINTREE_AUTHORIZATION = "braintreeJsAuthorisation";
    final static String NONCE_PARAMETER = "nonce";

    private final static int IDLE = 0x00;
    private final static int ONSELECT = 0x01;
    private final static int GETNONCE = 0x02;
    private final static int FINALIZE = 0x03;
    private final static int REDIRECT = 0x04;

    private final OperationService operationService;
    private Context applicationContext;
    private ProcessPaymentData processPaymentData;
    private Bundle fragmentResult;
    private String providerCode;
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
    public void reset() {
        this.state = IDLE;
        this.processPaymentData = null;
        this.applicationContext = null;
        this.fragmentResult = null;
        this.providerCode = null;
        operationService.stop();
    }

    @Override
    public boolean resume() {
        switch (state) {
            case REDIRECT:
                handleRedirectResult();
                return true;
            case GETNONCE:
                handleGetNonceResult();
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

        notifyOnProcessPaymentActive(false);
        Operation operation = createOperation(processPaymentData, PaymentLinkType.ONSELECT);
        operationService.postOperation(operation, applicationContext);
    }

    private void handleGetNonceResult() {
        if (fragmentResult == null) {
            closeWithProcessErrorMessage("Missing GooglePayBraintree fragment result after onResume");
            return;
        }
        if (providerCode == null) {
            closeWithProcessErrorMessage("Missing GooglePayBraintree provider code");
            return;
        }
        if (!(fragmentResult.containsKey(BRAINTREE_NONCE))) {
            Exception exception = (Exception) fragmentResult.getSerializable(BRAINTREE_ERROR);
            closeWithProcessPaymentInterrupted(exception);
            return;
        }
        PaymentMethodNonce nonce = fragmentResult.getParcelable(BRAINTREE_NONCE);
        ProviderParameters providerRequest = new ProviderParameters();
        providerRequest.setProviderCode(providerCode);

        List<Parameter> params = new ArrayList<>();
        Parameter param = new Parameter();
        param.setName(NONCE_PARAMETER);
        param.setValue(nonce.getString());
        params.add(param);
        providerRequest.setParameters(params);

        finalizePayment(providerRequest);
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

    private void finalizePayment(final ProviderParameters providerRequest) {
        state = FINALIZE;
        notifyOnProcessPaymentActive(true);

        Operation operation = createOperation(processPaymentData, PaymentLinkType.OPERATION);
        if (providerRequest != null) {
            operation.setProviderRequest(providerRequest);
        }
        operationService.postOperation(operation, applicationContext);
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
        // for presetting the method, the nonce is not required to finalize it.
        if (PRESET.equals(processPaymentData.getOperationType())) {
            finalizePayment(null);
            return;
        }
        state = GETNONCE;
        String braintreeAuthorization = PaymentUtils.getProviderParameterValue(BRAINTREE_AUTHORIZATION, operationResult);
        if (TextUtils.isEmpty((braintreeAuthorization))) {
            closeWithProcessErrorMessage("Missing GooglePayBraintree [" + BRAINTREE_AUTHORIZATION + "] parameter");
            return;
        }

        String providerCode = PaymentUtils.getProviderCode(operationResult);
        if (TextUtils.isEmpty(providerCode)) {
            closeWithProcessErrorMessage("Missing GooglePayBraintree provider code");
            return;
        } else {
            this.providerCode = providerCode;
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

        if (!(PROCEED.equals(interaction.getCode()) && requiresRedirect(operationResult))) {
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

    private void closeWithProcessPaymentInterrupted(final Exception exception) {
        reset();
        Log.w(TAG, "closeWithProcessPaymentInterrupted", exception);
        notifyOnProcessPaymentInterrupted(exception);
    }

    private void closeWithProcessPaymentResult(final CheckoutResult checkoutResult) {
        reset();
        Log.i(TAG, "closeWithProcessPaymentResult: " + checkoutResult);
        notifyOnProcessPaymentResult(checkoutResult);
    }
}
