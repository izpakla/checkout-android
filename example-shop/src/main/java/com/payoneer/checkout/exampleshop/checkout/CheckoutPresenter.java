/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.exampleshop.checkout;

import static com.payoneer.checkout.model.RedirectType.SUMMARY;
import static com.payoneer.checkout.ui.PaymentActivityResult.RESULT_CODE_ERROR;
import static com.payoneer.checkout.ui.PaymentActivityResult.RESULT_CODE_PROCEED;

import java.util.Objects;

import com.payoneer.checkout.model.Interaction;
import com.payoneer.checkout.model.InteractionCode;
import com.payoneer.checkout.model.OperationResult;
import com.payoneer.checkout.model.Redirect;
import com.payoneer.checkout.ui.PaymentActivityResult;
import com.payoneer.checkout.ui.PaymentResult;

/**
 * CheckoutPresenter takes care of handling the response from the Android Checkout SDK.
 */
final class CheckoutPresenter {

    private final CheckoutView view;

    /**
     * Construct a new CheckoutPresenter
     *
     * @param view the checkout view
     */
    CheckoutPresenter(CheckoutView view) {
        this.view = view;
    }

    /**
     * Handle the PaymentActivityResult received from Payoneer Checkout
     *
     * @param activityResult the result received
     */
    void handlePaymentActivityResult(PaymentActivityResult activityResult) {
        PaymentResult paymentResult = activityResult.getPaymentResult();
        switch (activityResult.getResultCode()) {
            case RESULT_CODE_PROCEED:
                handlePaymentResultProceed(paymentResult);
                break;
            case RESULT_CODE_ERROR:
                handlePaymentResultError(paymentResult);
                break;
        }
    }

    private void handlePaymentResultProceed(PaymentResult result) {
        Interaction interaction = result.getInteraction();
        if (interaction == null) {
            return;
        }
        OperationResult op = result.getOperationResult();
        if (op != null) {
            Redirect redirect = op.getRedirect();
            if (redirect != null && Objects.equals(SUMMARY, redirect.getType())) {
                view.showPaymentSummary();
                return;
            }
        }
        view.showPaymentConfirmation();
    }

    private void handlePaymentResultError(PaymentResult result) {
        Interaction interaction = result.getInteraction();
        switch (interaction.getCode()) {
            case InteractionCode.ABORT:
                if (!result.isNetworkFailure()) {
                    view.stopPaymentWithErrorMessage();
                }
                break;
            case InteractionCode.VERIFY:
                // VERIFY means that a charge request has been made but the status of the payment could
                // not be verified by the Payoneer Checkout library, i.e. because of a network error
                view.stopPaymentWithErrorMessage();
        }
    }
}
