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
import android.os.Bundle;
import androidx.fragment.app.Fragment;

/**
 * Class for handling the interaction between the PaymentService and e.g. a ViewModel.
 */
public final class PaymentServiceInteractor {

    private Observer observer;
    private PaymentService paymentService;

    /**
     * Load the PaymentService given the networkCode and paymentMethod. This will use the PaymentServiceLookup
     * to locate the appropriate payment service.
     *
     * @param networkCode code of the network e.g. VISA
     * @param paymentMethod method of the payment e.g. CREDIT_CARD
     * @throws PaymentException when the PaymentService could not be found or loaded
     */
    public void loadPaymentService(final String networkCode, final String paymentMethod) throws PaymentException {
        if (paymentService != null) {
            paymentService.setListener(null);
            paymentService.stop();
        }
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
            public void onProcessPaymentResult(final CheckoutResult checkoutResult) {
                if (observer != null) {
                    observer.onProcessPaymentResult(checkoutResult);
                }
            }
        });
    }

    /**
     * Ask the PaymentService to resume, e.g. if the PaymentService is waiting
     * for a redirect result.
     *
     * @return true when resumed, false otherwise
     */
    public boolean onResume() {
        return (paymentService != null) && paymentService.resume();
    }

    /**
     * Notify PaymentService that it will be stopped.
     * For example if the LifeCycleOwner is paused.
     */
    public void onStop() {
        if (paymentService != null) {
            paymentService.stop();
        }
    }

    public void onFragmentResult(final Bundle fragmentResult) {
        if (paymentService != null) {
            paymentService.onFragmentResult(fragmentResult);
        }
    }

    public void setObserver(final PaymentServiceInteractor.Observer observer) {
        this.observer = observer;
    }

    public void processPayment(final processPaymentData processPaymentData, final Context applicationContext) {
        if (paymentService == null) {
            throw new IllegalStateException("PaymentService must first be loaded by this interactor");
        }
        if (!paymentService.isActive()) {
            paymentService.processPayment(processPaymentData, applicationContext);
        }
    }

    /**
     * Observer interface for listening to events from this PaymentService interactor.
     */
    public interface Observer {

        void showFragment(final Fragment fragment);

        void onProcessPaymentActive();

        void onProcessPaymentResult(final CheckoutResult checkoutResult);
    }
}
