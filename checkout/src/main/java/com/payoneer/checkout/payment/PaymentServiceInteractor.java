/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.payment;

import com.payoneer.checkout.CheckoutResult;
import com.payoneer.checkout.core.PaymentException;

import android.content.Context;
import androidx.fragment.app.Fragment;

public class PaymentServiceInteractor {

    private Observer observer;
    private PaymentService paymentService;

    public PaymentServiceInteractor() {
    }

    public void loadPaymentService(final String code, final String paymentMethod) throws PaymentException {
        paymentService = PaymentServiceLookup.createService(code, paymentMethod);
        if (paymentService == null) {
            throw new PaymentException("Missing PaymentService for: " + code + ", " + paymentMethod);
        }
        paymentService.setListener(new PaymentServiceListener() {

            @Override
            public void showCustomFragment(final Fragment customFragment) {
                if (observer != null) {
                    observer.showCustomFragment(customFragment);
                }
            }

            @Override
            public void onProcessPaymentActive() {
                if (observer != null) {
                    observer.onProcessPaymentActive();
                }
            }

            @Override
            public void onDeleteAccountActive() {
                if (observer != null) {
                    observer.onDeleteAccountActive();
                }
            }

            @Override
            public void onProcessPaymentResult(final CheckoutResult checkoutResult) {
                if (observer != null) {
                    observer.onProcessPaymentResult(checkoutResult);
                }
            }

            @Override
            public void onDeleteAccountResult(final CheckoutResult checkoutResult) {
                if (observer != null) {
                    observer.onDeleteAccountResult(checkoutResult);
                }
            }
        });
    }

    public boolean onResume() {
        if (paymentService != null && paymentService.isPending()) {
            paymentService.resume();
            return true;
        }
        return false;
    }

    public void onStop() {
        if (paymentService != null) {
            paymentService.onStop();
        }
    }

    public void setObserver(final PaymentServiceInteractor.Observer observer) {
        this.observer = observer;
    }

    public void deleteAccount(final RequestData requestData, final Context applicationContext) {
        if (paymentService == null) {
            throw new IllegalStateException("PaymentService must first be set before deleting an account");
        }
        paymentService.deleteAccount(requestData, applicationContext);
    }

    public void processPayment(final RequestData requestData, final Context applicationContext) {
        if (paymentService == null) {
            throw new IllegalStateException("PaymentService must first be set before deleting an account");
        }
        paymentService.processPayment(requestData, applicationContext);
    }

    public interface Observer {

        void showCustomFragment(final Fragment customFragment);

        void onProcessPaymentActive();

        void onDeleteAccountActive();

        void onProcessPaymentResult(final CheckoutResult checkoutResult);

        void onDeleteAccountResult(final CheckoutResult checkoutResult);
    }
}
