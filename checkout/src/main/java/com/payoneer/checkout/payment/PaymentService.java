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

import com.payoneer.checkout.core.PaymentException;
import com.payoneer.checkout.model.AccountInputData;
import com.payoneer.checkout.model.OperationData;
import com.payoneer.checkout.model.OperationResult;
import com.payoneer.checkout.model.Redirect;
import com.payoneer.checkout.operation.Operation;
import com.payoneer.checkout.redirect.RedirectRequest;
import com.payoneer.checkout.redirect.RedirectService;

import android.content.Context;

/**
 * Base class for payment services, a payment service is responsible for processing a payment through the supported payment network.
 */
public abstract class PaymentService {

    protected PaymentServiceListener listener;

    public void setListener(final PaymentServiceListener listener) {
        this.listener = listener;
    }

    /**
     * Called when the payment service will be stopped, e.g. the user clicked the back button
     */
    public abstract void onStop();

    /**
     * Resume the service, this should only be called if isPending returns true.
     *
     * @return true when the PaymentService is resumed, false otherwise
     */
    public abstract boolean onResume();

    /**
     * Ask the payment service to process the payment.
     *
     * @param processPaymentData containing the data to make the payment request
     */
    public abstract void processPayment(final processPaymentData processPaymentData, final Context applicationContext);

    /**
     * Create a redirect request and open a custom chrome tab to continue processing the request.
     *
     * @param context to be used to make the redirect call
     * @param requestCode code to identify the origin request
     * @param operationResult containing the redirect details like redirect URL
     * @return newly created RedirectRequest
     * @throws PaymentException when an error occurred while redirecting
     */
    protected RedirectRequest redirect(final Context context, final int requestCode, final OperationResult operationResult)
        throws PaymentException {
        RedirectRequest redirectRequest = RedirectRequest.fromOperationResult(requestCode, operationResult);

        if (!RedirectService.supports(context, redirectRequest)) {
            throw new PaymentException("The Redirect payment method is not supported by the Android-SDK");
        }
        RedirectService.redirect(context, redirectRequest);
        return redirectRequest;
    }

    public static Operation createOperation(final processPaymentData processPaymentData, final String link) {
        OperationData operationData = new OperationData();
        operationData.setAccount(new AccountInputData());

        processPaymentData.getPaymentInputValues().copyInto(operationData);
        return new Operation(processPaymentData.getLink(link), operationData);
    }

    public static boolean requiresRedirect(final OperationResult operationResult) {
        Redirect redirect = operationResult.getRedirect();
        String type = redirect != null ? redirect.getType() : null;
        return PROVIDER.equals(type) || HANDLER3DS2.equals(type);
    }

    public static String getErrorInteractionCode(final String operationType) {
        return CHARGE.equals(operationType) || PAYOUT.equals(operationType) ? VERIFY : ABORT;
    }
}
