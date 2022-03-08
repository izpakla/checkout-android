/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.exampleshop.checkout;

import static com.payoneer.checkout.model.RedirectType.SUMMARY;
import static com.payoneer.checkout.CheckoutActivityResult.RESULT_CODE_ERROR;
import static com.payoneer.checkout.CheckoutActivityResult.RESULT_CODE_PROCEED;

import com.payoneer.checkout.model.Interaction;
import com.payoneer.checkout.model.InteractionCode;
import com.payoneer.checkout.CheckoutActivityResult;
import com.payoneer.checkout.CheckoutResult;
import com.payoneer.checkout.util.PaymentUtils;

/**
 * CheckoutPresenter takes care of handling the response from the Checkout SDK.
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
     * Handle the CheckoutActivityResult received from the Checkout SDK.
     *
     * @param activityResult containing the checkout activity result
     */
    void handleCheckoutActivityResult(CheckoutActivityResult activityResult) {
        CheckoutResult checkoutResult = activityResult.getCheckoutResult();
        switch (activityResult.getResultCode()) {
            case RESULT_CODE_PROCEED:
                handleCheckoutResultProceed(checkoutResult);
                break;
            case RESULT_CODE_ERROR:
                handleCheckoutResultError(checkoutResult);
                break;
        }
    }

    private void handleCheckoutResultProceed(CheckoutResult result) {
        Interaction interaction = result.getInteraction();
        if (interaction == null) {
            return;
        }
        if (PaymentUtils.containsRedirectType(result.getOperationResult(), SUMMARY)) {
            view.showPaymentSummary();
            return;
        }
        view.showPaymentConfirmation();
    }

    private void handleCheckoutResultError(CheckoutResult result) {
        Interaction interaction = result.getInteraction();
        switch (interaction.getCode()) {
            case InteractionCode.ABORT:
                if (!result.isNetworkFailure()) {
                    view.stopPaymentWithErrorMessage();
                }
                break;
            case InteractionCode.VERIFY:
                // VERIFY means that a charge request has been made but the status of the payment could
                // not be verified by the Checkout SDK, i.e. because of a network error
                view.stopPaymentWithErrorMessage();
        }
    }
}
