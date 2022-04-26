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
import android.util.Log;

public class PaymentSessionInteractor {

    private final PaymentSessionService sessionService;
    private final CheckoutConfiguration configuration;
    private Observer observer;

    public PaymentSessionInteractor(final CheckoutConfiguration configuration) {
        this.configuration = configuration;
        sessionService = new PaymentSessionService();
        sessionService.setListener(new PaymentSessionListener() {
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
        });
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

    public interface Observer {

        void onPaymentSessionSuccess(final PaymentSession paymentSession);

        void onPaymentSessionError(final CheckoutResult checkoutResult);

    }
}
