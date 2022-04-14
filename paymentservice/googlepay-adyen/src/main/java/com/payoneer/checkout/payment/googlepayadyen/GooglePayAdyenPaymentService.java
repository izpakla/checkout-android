/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.payment.googlepayadyen;

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

import android.util.Log;
import androidx.fragment.app.Fragment;

/**
 * Create a new BasicNetworkService, this service is a basic implementation
 * of the payment service that handles credit/debit cards and redirect networks.
 */
public class GooglePayAdyenPaymentService extends PaymentService {

    private final static int PROCESSPAYMENT_REQUEST_CODE = 0;
    private final static int DELETEACCOUNT_REQUEST_CODE = 1;

    private final int ONSELECT = 0x00;
    private final int REQUIRE_TOKEN = 0x01;
    private final int FINALIZE_PAYMENT = 0x02;

    private final OperationService operationService;
    private RequestData requestData;
    private RedirectRequest redirectRequest;

    private int state;

    /**
     * Create a new BasicNetworkService, this service is a basic implementation
     * of the payment service that handles credit/debit cards and redirect networks.
     */
    public GooglePayAdyenPaymentService() {
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
        Log.i("AAA", "Processing GooglePay Adyen");
        this.requestData = requestData;
        this.redirectRequest = null;

        state = ONSELECT;
        Operation operation = createOperation(requestData, PaymentLinkType.ONSELECT);
        operationService.postOperation(operation, presenter.getApplicationContext());
    }

    @Override
    public void deleteAccount(final RequestData requestData) {
        this.requestData = requestData;
        this.redirectRequest = null;

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

        if (request.getRequestCode() == PROCESSPAYMENT_REQUEST_CODE) {
            presenter.onProcessPaymentResult(checkoutResult);
        } else {
            presenter.onDeleteAccountResult(checkoutResult);
        }
    }

    private void handleProcessPaymentSuccess(OperationResult operationResult) {
        switch (state) {
            case ONSELECT:
                handleProcessOnSelectSuccess(operationResult);
                break;
            case FINALIZE_PAYMENT:
                handleFinalizePaymentSuccess(operationResult);
        }
    }

    private void handleProcessOnSelectSuccess(final OperationResult operationResult) {
        Log.i("AAA", "showFragment");
        Fragment fragment = GooglePayAdyenFragment.newInstance();
        presenter.showFragment(fragment);
    }

    private void handleFinalizePaymentSuccess(final OperationResult operationResult) {
        Interaction interaction = operationResult.getInteraction();
        CheckoutResult checkoutResult = new CheckoutResult(operationResult);
        Log.i("checkout-sdk", "handleProcessPaymentSuccess: " + checkoutResult);

        if (!PROCEED.equals(interaction.getCode())) {
            presenter.onProcessPaymentResult(checkoutResult);
            return;
        }
        if (!requiresRedirect(operationResult)) {
            presenter.onProcessPaymentResult(checkoutResult);
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
        presenter.onProcessPaymentResult(checkoutResult);
    }

    private void handleDeleteAccountSuccess(OperationResult operationResult) {
        Interaction interaction = operationResult.getInteraction();
        CheckoutResult checkoutResult = new CheckoutResult(operationResult);
        Log.i("checkout-sdk", "handleDeleteAccountSuccess: " + checkoutResult);

        if (!PROCEED.equals(interaction.getCode())) {
            presenter.onDeleteAccountResult(checkoutResult);
            return;
        }
        if (!requiresRedirect(operationResult)) {
            presenter.onDeleteAccountResult(checkoutResult);
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
        presenter.onDeleteAccountResult(checkoutResult);
    }
}