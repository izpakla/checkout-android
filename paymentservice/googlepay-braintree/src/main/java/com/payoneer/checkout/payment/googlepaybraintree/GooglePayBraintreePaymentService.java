/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.payment.googlepaybraintree;

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
import com.payoneer.checkout.util.PaymentUtils;

import android.util.Log;
import androidx.fragment.app.Fragment;

/**
 * Create a new BasicNetworkService, this service is a basic implementation
 * of the payment service that handles credit/debit cards and redirect networks.
 */
public class GooglePayBraintreePaymentService extends PaymentService {

    private final int STOPPED = 0x00;
    private final int DELETE_ACCOUNT = 0x01;
    private final int PROCESS_ONSELECT = 0x02;
    private final int PROCESS_GETTOKEN = 0x03;
    private final int PROCESS_FINALIZE = 0x04;

    private final OperationService operationService;
    private RequestData requestData;
    private RedirectRequest redirectRequest;

    private int state;

    /**
     * Create a new BasicNetworkService, this service is a basic implementation
     * of the payment service that handles credit/debit cards and redirect networks.
     */
    public GooglePayBraintreePaymentService() {
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
            handleRedirectResult(redirectRequest);
            redirectRequest = null;
        }
    }

    @Override
    public boolean isPending() {
        return redirectRequest != null;
    }

    @Override
    public void processPayment(final RequestData requestData) {
        this.requestData = requestData;
        this.redirectRequest = null;
        this.state = PROCESS_ONSELECT;

        Operation operation = createOperation(requestData, PaymentLinkType.ONSELECT);
        operationService.postOperation(operation, presenter.getApplicationContext());
    }

    @Override
    public void deleteAccount(final RequestData requestData) {
        this.requestData = requestData;
        this.redirectRequest = null;
        this.state = DELETE_ACCOUNT;

        DeleteAccount deleteAccount = createDeleteAccount(requestData);
        operationService.deleteAccount(deleteAccount, presenter.getApplicationContext());
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
        Log.i("checkout-sdk", "onRedirectResult: " + checkoutResult);

        if (state == DELETE_ACCOUNT) {
            presenter.onDeleteAccountResult(requestData, checkoutResult);
        } else {
            presenter.onProcessPaymentResult(requestData, checkoutResult);
        }
    }

    private void handleProcessPaymentSuccess(OperationResult operationResult) {
        switch (state) {
            case PROCESS_ONSELECT:
                handleProcessOnSelectSuccess(operationResult);
                break;
            case PROCESS_FINALIZE:
                handleFinalizePaymentSuccess(operationResult);
        }
    }

    private void handleProcessOnSelectSuccess(final OperationResult operationResult) {
        PaymentUtils.getProviderParameterValue("", operationResult);
        state = PROCESS_GETTOKEN;
        Fragment fragment = GooglePayBraintreeFragment.newInstance();
        presenter.showCustomFragment(fragment);
    }

    private void handleFinalizePaymentSuccess(final OperationResult operationResult) {
        Interaction interaction = operationResult.getInteraction();
        CheckoutResult checkoutResult = new CheckoutResult(operationResult);
        Log.i("checkout-sdk", "handleProcessPaymentSuccess: " + checkoutResult);

        if (!PROCEED.equals(interaction.getCode())) {
            presenter.onProcessPaymentResult(requestData, checkoutResult);
            return;
        }
        if (!requiresRedirect(operationResult)) {
            presenter.onProcessPaymentResult(requestData, checkoutResult);
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
        presenter.onProcessPaymentResult(requestData, checkoutResult);
    }

    private void handleDeleteAccountSuccess(OperationResult operationResult) {
        Interaction interaction = operationResult.getInteraction();
        CheckoutResult checkoutResult = new CheckoutResult(operationResult);
        Log.i("checkout-sdk", "handleDeleteAccountSuccess: " + checkoutResult);

        if (!PROCEED.equals(interaction.getCode())) {
            presenter.onDeleteAccountResult(requestData, checkoutResult);
            return;
        }
        if (!requiresRedirect(operationResult)) {
            presenter.onDeleteAccountResult(requestData, checkoutResult);
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
        presenter.onDeleteAccountResult(requestData, checkoutResult);
    }

    private void closeWithDeleteAccountResult(CheckoutResult checkoutResult) {
        RequestData data = this.requestData;
        this.requestData = null;
        this.state = STOPPED;
        presenter.onDeleteAccountResult(requestData, checkoutResult);
    }
}