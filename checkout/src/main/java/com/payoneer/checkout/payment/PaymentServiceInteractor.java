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

/**
 * Class for handling the interaction between the PaymentService and e.g. a ViewModel.
 */
public final class PaymentServiceInteractor {

    private Observer observer;
    private PaymentService paymentService;

    /**
     * Load the PaymentService given the code and paymentMethod. This will use the PaymentServiceLookup
     *
     * @param networkCode code of the network e.g. VISA
     * @param paymentMethod method of the payment e.g. CREDIT_CARD
     * @throws PaymentException when the PaymentService could not be found or loaded
     */
    public void loadPaymentService(final String networkCode, final String paymentMethod) throws PaymentException {
        paymentService = PaymentServiceLookup.createService(networkCode, paymentMethod);
        if (paymentService == null) {
            throw new PaymentException("Missing PaymentService for: " + networkCode + ", " + paymentMethod);
        }
        paymentService.setListener(new PaymentServiceListener() {

            @Override
            public void showFragment(final Fragment fragment) {
                if (observer != null) {
                    observer.showFragment(fragment);
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

    /**
     * Ask the PaymentService if it needs to be resumed, e.g. if the PaymentService is waiting
     * for a redirect result.
     *
     * @return true when resumed, false otherwise
     */
    public boolean onResume() {
        if (paymentService != null && paymentService.onResume()) {
            return true;
        }
        return false;
    }

    /**
     * Notify PaymentService that it will be stopped. For example if the Activity is paused.
     */
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

    /**
     * Observer interface for listening to events from this PaymentService interactor.
     */
    public interface Observer {

        void showFragment(final Fragment fragment);

        void onProcessPaymentActive();

        void onDeleteAccountActive();

        void onProcessPaymentResult(final CheckoutResult checkoutResult);

        void onDeleteAccountResult(final CheckoutResult checkoutResult);
    }
}
