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

    protected PaymentServiceController controller;

    public abstract void onStop();

    public abstract boolean isPaused();

    public abstract void resume();

    public abstract void processPayment(final RequestData requestData, final Context context);

    public abstract void deleteAccount(final RequestData requestData, final Context context);

    public void setController(final PaymentServiceController controller) {
        this.controller = controller;
    }

    protected Operation createOperation(final RequestData requestData, final String link) {
        OperationData operationData = new OperationData();
        operationData.setAccount(new AccountInputData());

        requestData.getPaymentInputValues().copyInto(operationData);
        return new Operation(requestData.getLink(link), operationData);
    }

    protected DeleteAccount createDeleteAccount(final RequestData requestData) {
        URL url = requestData.getLink(PaymentLinkType.SELF);
        return new DeleteAccount(url);
    }

    protected boolean requiresRedirect(final OperationResult operationResult) {
        Redirect redirect = operationResult.getRedirect();
        String type = redirect != null ? redirect.getType() : null;
        return PROVIDER.equals(type) || HANDLER3DS2.equals(type);
    }

    protected String getErrorInteractionCode(final String operationType) {
        return CHARGE.equals(operationType) || PAYOUT.equals(operationType) ? VERIFY : ABORT;
    }

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
