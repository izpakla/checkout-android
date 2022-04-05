/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.payment.adyengooglepay;

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

import com.payoneer.checkout.core.PaymentException;
import com.payoneer.checkout.model.Interaction;
import com.payoneer.checkout.model.OperationResult;
import com.payoneer.checkout.model.Redirect;
import com.payoneer.checkout.network.DeleteAccount;
import com.payoneer.checkout.network.Operation;
import com.payoneer.checkout.payment.PaymentRequest;
import com.payoneer.checkout.payment.PaymentService;
import com.payoneer.checkout.redirect.RedirectRequest;
import com.payoneer.checkout.ui.PaymentResult;
import com.payoneer.checkout.ui.service.OperationListener;
import com.payoneer.checkout.ui.service.OperationService;
import com.payoneer.checkout.util.PaymentResultHelper;

import android.content.Context;
import android.util.Log;

public class GooglePayAdyenPaymentService extends PaymentService {

    private final static int DELETEACCOUNT_REQUEST_CODE = 1;
    private final String TAG = GooglePayAdyenPaymentService.class.getSimpleName();
    private final OperationService operationService;
    private String operationType;
    private PaymentRequest request;

    public GooglePayAdyenPaymentService() {
        operationService = new OperationService();
        operationService.setListener(new OperationListener() {
            @Override
            public void onDeleteAccountSuccess(final OperationResult operationResult) {
                handleDeleteAccountSuccess(operationResult);
            }

            @Override
            public void onDeleteAccountError(final Throwable cause) {
                handleDeleteAccountError(cause);
            }

            @Override
            public void onOperationSuccess(final OperationResult operationResult) {
                handleProcessPaymentSuccess(operationResult);
            }

            @Override
            public void onOperationError(final Throwable cause) {
                handleProcessPaymentError(cause);
            }
        });
    }

    private void handleProcessPaymentSuccess(final OperationResult operationResult) {
        String auth = getProviderParameterValue("braintreeJsAuthorisation", operationResult);
        Log.i(TAG, "Show Google Adyen fragment now");
        listener.showGooglePay(auth);
    }

    private String getProviderParameterValue(final String braintreeJsAuthorisation, final OperationResult operationResult) {
        return null;
    }

    private void handleProcessPaymentError(final Throwable cause) {
        String code = getErrorInteractionCode(operationType);
        PaymentResult paymentResult = PaymentResultHelper.fromThrowable(code, cause);

        Log.i(TAG, "handleProcessPaymentError: " + paymentResult);
        listener.onProcessPaymentResult(RESULT_CODE_ERROR, paymentResult);
    }

    private String getErrorInteractionCode(String operationType) {
        return CHARGE.equals(operationType) || PAYOUT.equals(operationType) ? VERIFY : ABORT;
    }

    private void handleDeleteAccountSuccess(final OperationResult operationResult) {
        Interaction interaction = operationResult.getInteraction();
        PaymentResult paymentResult = new PaymentResult(operationResult);
        Log.i(TAG, "handleDeleteAccountSuccess: " + paymentResult);

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

    @Override
    public void stop() {
        operationService.stop();
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

    private boolean requiresRedirect(OperationResult operationResult) {
        Redirect redirect = operationResult.getRedirect();
        String type = redirect != null ? redirect.getType() : null;
        return PROVIDER.equals(type) || HANDLER3DS2.equals(type);
    }

    private void handleDeleteAccountError(Throwable cause) {
        PaymentResult paymentResult = PaymentResultHelper.fromThrowable(ABORT, cause);
        Log.i(TAG, "handleDeleteAccountError: " + paymentResult);
        listener.onDeleteAccountResult(RESULT_CODE_ERROR, paymentResult);
    }
}
