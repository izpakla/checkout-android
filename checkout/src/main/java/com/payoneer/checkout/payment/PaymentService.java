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

import com.payoneer.checkout.CheckoutResult;
import com.payoneer.checkout.core.PaymentException;
import com.payoneer.checkout.model.AccountInputData;
import com.payoneer.checkout.model.OperationData;
import com.payoneer.checkout.model.OperationResult;
import com.payoneer.checkout.model.Redirect;
import com.payoneer.checkout.operation.Operation;
import com.payoneer.checkout.redirect.RedirectRequest;
import com.payoneer.checkout.redirect.RedirectService;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

/**
 * Base class for payment services, a payment service is responsible for processing a payment through the supported payment network.
 */
public abstract class PaymentService {

    protected PaymentServiceListener listener;

    public void setListener(final PaymentServiceListener listener) {
        this.listener = listener;
    }

    /**
     * Called to stop this payment service, e.g. the user navigated to the browser window
     */
    public abstract void stop();

    /**
     * The payment service should reset itself and remove all cached data
     */
    public abstract void reset();

    /**
     * Resume this PaymentService.
     *
     * @return true when this PaymentService resumed, false otherwise
     */
    public abstract boolean resume();

    /**
     * Is this payment service currently active
     */
    public abstract boolean isActive();

    /**
     * After the fragment is finished, it may handover result to the PaymentService
     *
     * @param fragmentResult result provided by the fragment
     */
    public abstract void onFragmentResult(final Bundle fragmentResult);

    /**
     * Ask the payment service to process the payment.
     *
     * @param processPaymentData containing the data to make the payment request
     */
    public abstract void processPayment(final ProcessPaymentData processPaymentData, final Context applicationContext);

    /**
     * Helper method to notify the listener of the process payment result.
     */
    protected void notifyOnProcessPaymentResult(final CheckoutResult checkoutResult) {
        if (listener != null) {
            listener.onProcessPaymentResult(checkoutResult);
        }
    }

    /**
     * Helper method to notify the listener that the payment process has been interrupted.
     * Interruption should not have an affect on the state of the list.
     */
    protected void notifyOnProcessPaymentInterrupted(final Exception exception) {
        if (listener != null) {
            listener.onProcessPaymentInterrupted(exception);
        }
    }

    /**
     * Helper method to notify the listener of the process payment active state.
     *
     * @param finalizing the processing of the payment is in its final state.
     */
    protected void notifyOnProcessPaymentActive(final boolean finalizing) {
        if (listener != null) {
            listener.onProcessPaymentActive(finalizing);
        }
    }

    /**
     * Helper method to notify the listener that the fragment should be shown.
     *
     * @param fragment to be shown to the user
     */
    protected void notifyShowFragment(final Fragment fragment) {
        if (listener != null) {
            listener.showFragment(fragment);
        }
    }

    /**
     * Create a redirect request and open a custom chrome tab to continue processing the request.
     *
     * @param requestCode code to identify the origin request
     * @param operationResult containing the redirect details like redirect URL
     * @param applicationContext to be used to make the redirect call
     * @return newly created RedirectRequest
     * @throws PaymentException when an error occurred while redirecting
     */
    protected RedirectRequest redirect(final int requestCode, final OperationResult operationResult, final Context applicationContext)
        throws PaymentException {
        RedirectRequest redirectRequest = RedirectRequest.fromOperationResult(requestCode, operationResult);

        if (!RedirectService.supports(applicationContext, redirectRequest)) {
            throw new PaymentException("The Redirect payment method is not supported by the Android-SDK");
        }
        RedirectService.redirect(applicationContext, redirectRequest);
        return redirectRequest;
    }

    public static Operation createOperation(final ProcessPaymentData processPaymentData, final String link) {
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
