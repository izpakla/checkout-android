/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout;

import static com.payoneer.checkout.CheckoutResult.EXTRA_CHECKOUT_RESULT;
import static com.payoneer.checkout.model.InteractionReason.CLIENTSIDE_ERROR;
import static com.payoneer.checkout.model.InteractionReason.COMMUNICATION_FAILURE;

import com.payoneer.checkout.core.PaymentException;
import com.payoneer.checkout.model.ErrorInfo;
import com.payoneer.checkout.model.Interaction;
import com.payoneer.checkout.model.InteractionCode;

import android.content.Intent;

/**
 * Class with helper methods to construct a CheckoutResult
 */
public final class CheckoutResultHelper {

    /**
     * Helper method to construct a default CheckoutResult from the error message
     *
     * @param errorMessage describing the error that occurred
     * @return the newly created CheckoutResult
     */
    public static CheckoutResult fromErrorMessage(final String errorMessage) {
        return fromErrorMessage(InteractionCode.ABORT, errorMessage);
    }

    /**
     * Helper method to construct a CheckoutResult with the provided Interaction.Code and error message
     *
     * @param interactionCode code used for creating the CheckoutResult
     * @param errorMessage describing the error that occurred
     * @return the newly created CheckoutResult
     */
    public static CheckoutResult fromErrorMessage(final String interactionCode, final String errorMessage) {
        Interaction interaction = new Interaction(interactionCode, CLIENTSIDE_ERROR);
        ErrorInfo errorInfo = new ErrorInfo(errorMessage, interaction);
        return new CheckoutResult(errorInfo);
    }

    /**
     * Helper method to construct a default CheckoutResult from the Throwable
     *
     * @param error the throwable that caused the error
     * @return the newly created CheckoutResult
     */
    public static CheckoutResult fromThrowable(final Throwable error) {
        return fromThrowable(InteractionCode.ABORT, error);
    }

    /**
     * Helper method to construct a default CheckoutResult from the Throwable object
     *
     * @param interactionCode code used for creating the CheckoutResult
     * @param error the throwable that caused the error
     * @return the newly created CheckoutResult
     */
    public static CheckoutResult fromThrowable(final String interactionCode, final Throwable error) {
        ErrorInfo errorInfo = null;
        boolean networkFailure = false;
        Throwable cause = error;

        if (error instanceof PaymentException) {
            PaymentException e = (PaymentException) error;
            errorInfo = e.getErrorInfo();
            networkFailure = e.getNetworkFailure();
            cause = e.getCause();
        }
        if (errorInfo == null) {
            String reason = networkFailure ? COMMUNICATION_FAILURE : CLIENTSIDE_ERROR;
            Interaction interaction = new Interaction(interactionCode, reason);
            errorInfo = new ErrorInfo(error.getMessage(), interaction);
        }
        return new CheckoutResult(errorInfo, cause);
    }

    /**
     * Put the CheckoutResult into the provided result intent.
     *
     * @param checkoutResult to be put inside the intent
     * @param intent into which this CheckoutResult should be stored
     */
    public static void putIntoResultIntent(final CheckoutResult checkoutResult, final Intent intent) {
        if (intent != null) {
            intent.putExtra(EXTRA_CHECKOUT_RESULT, checkoutResult);
        }
    }

    /**
     * Get the CheckoutResult from the result intent.
     *
     * @param intent containing the CheckoutResult
     * @return CheckoutResult or null if not stored in the intent
     */
    public static CheckoutResult fromResultIntent(final Intent intent) {
        if (intent != null) {
            return intent.getParcelableExtra(EXTRA_CHECKOUT_RESULT);
        }
        return null;
    }
}
