/*
 * Copyright (c) 2021 Payoneer Germany GmbH
 * https://payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.page;

import static com.payoneer.checkout.CheckoutActivityResult.RESULT_CODE_ERROR;
import static com.payoneer.checkout.CheckoutActivityResult.RESULT_CODE_PROCEED;

import com.payoneer.checkout.CheckoutConfiguration;
import com.payoneer.checkout.core.PaymentException;
import com.payoneer.checkout.localization.InteractionMessage;
import com.payoneer.checkout.model.Interaction;
import com.payoneer.checkout.CheckoutResult;
import com.payoneer.checkout.ui.model.PaymentSession;
import com.payoneer.checkout.ui.service.NetworkService;
import com.payoneer.checkout.ui.service.NetworkServiceLookup;
import com.payoneer.checkout.CheckoutResultHelper;

/**
 * Base class for payment presenters
 */
abstract class BasePaymentPresenter {

    final static int STOPPED = 0;
    final static int STARTED = 1;
    final static int PROCESS = 2;

    final BasePaymentView view;
    final CheckoutConfiguration configuration;
    int state;

    /**
     * Construct a new BasePaymentPresenter
     *
     * @param configuration self URL of the ListResult
     */
    BasePaymentPresenter(CheckoutConfiguration configuration, BasePaymentView view) {
        this.configuration = configuration;
        this.view = view;
    }

    boolean checkState(int state) {
        return this.state == state;
    }

    void setState(int state) {
        this.state = state;
    }

    void closeWithProceedCode(CheckoutResult result) {
        view.setPaymentResult(RESULT_CODE_PROCEED, result);
        view.close();
    }

    void closeWithErrorCode(String message) {
        CheckoutResult result = CheckoutResultHelper.fromErrorMessage(message);
        closeWithErrorCode(result);
    }

    void closeWithErrorCode(CheckoutResult result) {
        view.setPaymentResult(RESULT_CODE_ERROR, result);
        view.close();
    }

    NetworkService loadNetworkService(String code, String paymentMethod) throws PaymentException {
        NetworkService service = NetworkServiceLookup.createService(view.getActivity(), code, paymentMethod);
        if (service == null) {
            throw new PaymentException("Missing NetworkService for: " + code + ", " + paymentMethod);
        }
        return service;
    }

    InteractionMessage createInteractionMessage(Interaction interaction, PaymentSession session) {
        if (session == null) {
            return InteractionMessage.fromInteraction(interaction);
        }
        return InteractionMessage.fromOperationFlow(interaction, session.getListOperationType());
    }
}
