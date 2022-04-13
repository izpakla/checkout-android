/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.payment;

import static com.payoneer.checkout.model.InteractionCode.ABORT;
import static com.payoneer.checkout.model.InteractionCode.VERIFY;
import static com.payoneer.checkout.model.NetworkOperationType.CHARGE;
import static com.payoneer.checkout.model.NetworkOperationType.PAYOUT;
import static com.payoneer.checkout.model.RedirectType.HANDLER3DS2;
import static com.payoneer.checkout.model.RedirectType.PROVIDER;

import java.net.URL;

import com.payoneer.checkout.core.PaymentException;
import com.payoneer.checkout.core.PaymentLinkType;
import com.payoneer.checkout.model.AccountInputData;
import com.payoneer.checkout.model.OperationData;
import com.payoneer.checkout.model.OperationResult;
import com.payoneer.checkout.model.Redirect;
import com.payoneer.checkout.operation.DeleteAccount;
import com.payoneer.checkout.operation.Operation;
import com.payoneer.checkout.redirect.RedirectRequest;
import com.payoneer.checkout.redirect.RedirectService;

import android.content.Context;

/**
 * Interface for network services, a NetworkService is responsible for activating and
 * processing a payment through the supported payment network.
 */
public abstract class PaymentService {

    protected PaymentServicePresenter controller;

    /**
     *
     */
    public abstract void onStop();

    /**
     *
     * @return
     */
    public abstract boolean isPaused();

    /**
     *
     */
    public abstract void resume();

    /**
     *
     * @param requestData
     * @param applicationContext
     */
    public abstract void processPayment(final RequestData requestData, final Context applicationContext);

    /**
     *
     * @param requestData
     * @param applicationContext
     */
    public abstract void deleteAccount(final RequestData requestData, final Context applicationContext);

    /**
     *
     * @param controller
     */
    public void setController(final PaymentServicePresenter controller) {
        this.controller = controller;
    }

    /**
     *
     * @param requestData
     * @param link
     * @return
     */
    protected Operation createOperation(final RequestData requestData, final String link) {
        OperationData operationData = new OperationData();
        operationData.setAccount(new AccountInputData());

        requestData.getPaymentInputValues().copyInto(operationData);
        return new Operation(requestData.getLink(link), operationData);
    }

    /**
     *
     * @param requestData
     * @return
     */
    protected DeleteAccount createDeleteAccount(final RequestData requestData) {
        URL url = requestData.getLink(PaymentLinkType.SELF);
        return new DeleteAccount(url);
    }

    /**
     *
     * @param operationResult
     * @return
     */
    protected boolean requiresRedirect(final OperationResult operationResult) {
        Redirect redirect = operationResult.getRedirect();
        String type = redirect != null ? redirect.getType() : null;
        return PROVIDER.equals(type) || HANDLER3DS2.equals(type);
    }

    /**
     * Get the error interaction code.
     *
     * @param operationType
     * @return
     */
    protected String getErrorInteractionCode(final String operationType) {
        return CHARGE.equals(operationType) || PAYOUT.equals(operationType) ? VERIFY : ABORT;
    }

    /**
     * Create a redirect request and open a custom chrome tab to continue processing the request.
     *
     * @param requestCode code to identify the origin request
     * @param operationResult containing the redirect details like redirect URL
     * @return newly created RedirectRequest
     * @throws PaymentException when an error occurred while redirecting
     */
    protected RedirectRequest redirect(final int requestCode, final OperationResult operationResult) throws PaymentException {
        Context context = controller.getContext();
        RedirectRequest redirectRequest = RedirectRequest.fromOperationResult(requestCode, operationResult);

        if (!RedirectService.supports(context, redirectRequest)) {
            throw new PaymentException("The Redirect payment method is not supported by the Android-SDK");
        }
        RedirectService.redirect(context, redirectRequest);
        return redirectRequest;
    }
}
