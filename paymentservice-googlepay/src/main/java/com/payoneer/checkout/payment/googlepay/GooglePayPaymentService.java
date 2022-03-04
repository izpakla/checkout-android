/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.payment.googlepay;

import static com.payoneer.checkout.model.InteractionCode.ABORT;
import static com.payoneer.checkout.model.InteractionCode.PROCEED;
import static com.payoneer.checkout.model.InteractionCode.VERIFY;
import static com.payoneer.checkout.model.NetworkOperationType.CHARGE;
import static com.payoneer.checkout.model.NetworkOperationType.PAYOUT;
import static com.payoneer.checkout.model.RedirectType.HANDLER3DS2;
import static com.payoneer.checkout.model.RedirectType.PROVIDER;
import static com.payoneer.checkout.ui.PaymentActivityResult.RESULT_CODE_ERROR;
import static com.payoneer.checkout.ui.PaymentActivityResult.RESULT_CODE_PROCEED;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.payoneer.checkout.core.PaymentException;
import com.payoneer.checkout.model.Interaction;
import com.payoneer.checkout.model.Parameter;
import com.payoneer.checkout.model.ProviderParameters;
import com.payoneer.checkout.model.Redirect;
import com.payoneer.checkout.network.DeleteAccount;
import com.payoneer.checkout.network.Operation;
import com.payoneer.checkout.model.OperationResult;
import com.payoneer.checkout.payment.PaymentRequest;
import com.payoneer.checkout.payment.PaymentService;
import com.payoneer.checkout.payment.PaymentServiceListener;
import com.payoneer.checkout.redirect.RedirectRequest;
import com.payoneer.checkout.ui.PaymentResult;
import com.payoneer.checkout.ui.service.OperationListener;
import com.payoneer.checkout.ui.service.OperationService;
import com.payoneer.checkout.util.PaymentResultHelper;

import android.content.Context;
import android.util.Log;

/**
 * GooglePayPaymentService
 */
public final class GooglePayPaymentService extends PaymentService {

    private final static int PROCESSPAYMENT_REQUEST_CODE = 0;
    private final static int DELETEACCOUNT_REQUEST_CODE = 1;

    private final OperationService operationService;
    private String operationType;
    private PaymentRequest request;

    /**
     * Create a new BasicNetworkService, this service is a basic implementation
     * that sends an operation to the Payment API.
     */
    public GooglePayPaymentService() {
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

    public void makeGoogleCharge(String nonce, Context context) {
        ProviderParameters providerParams = new ProviderParameters();
        providerParams.setProviderCode("GOOGLEPAY");

        List<Parameter> params = new ArrayList<>();
        Parameter param = new Parameter();
        param.setName("nonce");
        param.setValue(nonce);
        params.add(param);
        providerParams.setParameters(params);
        request.setProviderRequest(providerParams);

        Operation operation = new Operation(request.getLink("operation"), request.getOperationData());
        operationService.postOperation(operation, context);
    }

    @Override
    public void processPayment(PaymentRequest request, Context context) {
        this.request = request;
        this.operationType = request.getOperationType();
        listener.showProgress(true);

        URL onSelectUrl = request.getLink("onSelect");
        if (onSelectUrl != null) {
            Operation operation = new Operation(request.getLink("onSelect"), request.getOperationData());
            operationService.postOperation(operation, context);
        }

    }

    @Override
    public void deleteAccount(DeleteAccount account, Context context) {
        this.operationType = account.getOperationType();
        listener.showProgress(true);
        operationService.deleteAccount(account, context);
    }

    @Override
    public void onRedirectResult(RedirectRequest request, OperationResult operationResult) {
    }

    private void handleProcessPaymentSuccess(OperationResult operationResult) {
        Log.i("AAA", "OperationResult: " + operationResult);
        String auth = getProviderParameterValue("braintreeJsAuthorisation", operationResult);
        Log.i("AAA", "showBarintree fragment in listener");
        listener.showGooglePay(auth);
    }

    private void handleProcessPaymentError(Throwable cause) {
        Log.i("AAA", "OperationResult error");
        String code = getErrorInteractionCode(operationType);
        PaymentResult paymentResult = PaymentResultHelper.fromThrowable(code, cause);

        Log.i("checkout-sdk", "handleProcessPaymentError: " + paymentResult);
        listener.onProcessPaymentResult(RESULT_CODE_ERROR, paymentResult);
    }

    private void handleDeleteAccountSuccess(OperationResult operationResult) {
        Interaction interaction = operationResult.getInteraction();
        PaymentResult paymentResult = new PaymentResult(operationResult);
        Log.i("checkout-sdk", "handleDeleteAccountSuccess: " + paymentResult);

        if (!PROCEED.equals(interaction.getCode())) {
            listener.onDeleteAccountResult(RESULT_CODE_ERROR, paymentResult);
            return;
        }
        if (requiresRedirect(operationResult)) {
            try {
                RedirectRequest request = RedirectRequest.fromOperationResult(DELETEACCOUNT_REQUEST_CODE, operationResult);
                listener.redirect(request);
            } catch (PaymentException e) {
                handleDeleteAccountError(e);
            }
            return;
        }
        listener.onDeleteAccountResult(RESULT_CODE_PROCEED, paymentResult);
    }

    private void handleDeleteAccountError(Throwable cause) {
        PaymentResult paymentResult = PaymentResultHelper.fromThrowable(ABORT, cause);
        Log.i("checkout-sdk", "handleDeleteAccountError: " + paymentResult);
        listener.onDeleteAccountResult(RESULT_CODE_ERROR, paymentResult);
    }

    private boolean requiresRedirect(OperationResult operationResult) {
        Redirect redirect = operationResult.getRedirect();
        String type = redirect != null ? redirect.getType() : null;
        return PROVIDER.equals(type) || HANDLER3DS2.equals(type);
    }

    private String getErrorInteractionCode(String operationType) {
        return CHARGE.equals(operationType) || PAYOUT.equals(operationType) ? VERIFY : ABORT;
    }

    private String getProviderParameterValue(String key, OperationResult result) {
        if (result == null) {
            return null;
        }
        ProviderParameters parameters = result.getProviderResponse();
        if (parameters == null) {
            return null;
        }
        List<Parameter> params = parameters.getParameters();
        if (params == null) {
            return null;
        }
        for (Parameter p : params) {
            if (p.getName().equals(key)) {
                return p.getValue();
            }
        }
        return null;
    }
}
