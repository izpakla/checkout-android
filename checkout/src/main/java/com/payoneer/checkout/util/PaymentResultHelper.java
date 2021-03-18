/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.util;

import static com.payoneer.checkout.model.InteractionReason.CLIENTSIDE_ERROR;
import static com.payoneer.checkout.model.InteractionReason.COMMUNICATION_FAILURE;

import com.payoneer.checkout.core.PaymentException;
import com.payoneer.checkout.model.ErrorInfo;
import com.payoneer.checkout.model.Interaction;
import com.payoneer.checkout.ui.PaymentResult;
import com.payoneer.checkout.model.InteractionCode;

import android.content.Intent;

/**
 * Class with helper methods to construct a PaymentResult
 */
public class PaymentResultHelper {

    /**
     * Helper method to construct a default PaymentResult from the error message
     *
     * @param errorMessage describing the error that occurred
     * @return the newly created PaymentResult
     */
    public static PaymentResult fromErrorMessage(String errorMessage) {
        return fromErrorMessage(InteractionCode.ABORT, errorMessage);
    }

    /**
     * Helper method to construct a PaymentResult with the provided Interaction.Code and error message
     *
     * @param interactionCode code used for creating the PaymentResult
     * @param errorMessage describing the error that occurred
     * @return the newly created PaymentResult
     */
    public static PaymentResult fromErrorMessage(String interactionCode, String errorMessage) {
        Interaction interaction = new Interaction(interactionCode, CLIENTSIDE_ERROR);
        ErrorInfo errorInfo = new ErrorInfo(errorMessage, interaction);
        return new PaymentResult(errorInfo);
    }

    /**
     * Helper method to construct a default PaymentResult from the Throwable
     *
     * @param error the throwable that caused the error
     * @return the newly created PaymentResult
     */
    public static PaymentResult fromThrowable(Throwable error) {
        return fromThrowable(InteractionCode.ABORT, error);
    }

    /**
     * Helper method to construct a default PaymentResult from the Throwable object
     *
     * @param interactionCode code used for creating the PaymentResult
     * @param error the throwable that caused the error
     * @return the newly created PaymentResult
     */
    public static PaymentResult fromThrowable(String interactionCode, Throwable error) {
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
        return new PaymentResult(errorInfo, cause);
    }

    /**
     * Put the PaymentResult into the provided result intent.
     *
     * @param paymentResult to be put inside the intent
     * @param intent into which this PaymentResult should be stored
     */
    public static void putIntoResultIntent(PaymentResult paymentResult, Intent intent) {
        if (intent != null) {
            intent.putExtra(PaymentResult.EXTRA_PAYMENT_RESULT, paymentResult);
        }
    }

    /**
     * Get the PaymentResult from the result intent.
     *
     * @param intent containing the PaymentResult
     * @return PaymentResult or null if not stored in the intent
     */
    public static PaymentResult fromResultIntent(Intent intent) {
        if (intent != null) {
            return intent.getParcelableExtra(PaymentResult.EXTRA_PAYMENT_RESULT);
        }
        return null;
    }
}
