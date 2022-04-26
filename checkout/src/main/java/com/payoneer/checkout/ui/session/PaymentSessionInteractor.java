/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.session;

import com.payoneer.checkout.CheckoutConfiguration;
import com.payoneer.checkout.CheckoutResult;
import com.payoneer.checkout.CheckoutResultHelper;
import com.payoneer.checkout.ui.model.PaymentSession;

import android.content.Context;

public class PaymentSessionInteractor implements PaymentSessionListener {

    private final PaymentSessionService sessionService;
    private final CheckoutConfiguration configuration;
    private Observer observer;

    public PaymentSessionInteractor(final CheckoutConfiguration configuration) {
        this.sessionService = new PaymentSessionService();
        this.configuration = configuration;
    }

    public void onStop() {
        sessionService.onStop();
    }

    public void setObserver(final PaymentSessionInteractor.Observer observer) {
        this.observer = observer;
    }

    public void loadPaymentSession(final Context applicationContext) {
        sessionService.loadPaymentSession(configuration, applicationContext);
    }

    @Override
    public void onPaymentSessionSuccess(final PaymentSession paymentSession) {
        if (observer != null) {
            observer.onPaymentSessionSuccess(paymentSession);
        }
    }

    @Override
    public void onPaymentSessionError(final Throwable cause) {
        CheckoutResult result = CheckoutResultHelper.fromThrowable(cause);
        if (observer != null) {
            observer.onPaymentSessionError(result);
        }
    }

    public interface Observer {

        void onPaymentSessionSuccess(final PaymentSession paymentSession);

        void onPaymentSessionError(final CheckoutResult checkoutResult);

    }
}
