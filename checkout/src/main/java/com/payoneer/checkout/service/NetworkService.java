/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.service;

import com.payoneer.checkout.form.Operation;
import com.payoneer.checkout.model.OperationResult;

import android.app.Activity;

/**
 * Interface for network services, a NetworkService is responsible for activating and
 * processing a payment through the supported payment network.
 */
public abstract class NetworkService {

    protected NetworkServicePresenter presenter;

    /**
     * Stop this NetworkService
     */
    public void stop() {
    }

    /**
     * Set the presenter in this NetworkService
     *
     * @param presenter the presenter to be set
     */
    public void setPresenter(NetworkServicePresenter presenter) {
        this.presenter = presenter;
    }

    /**
     * Process the payment through this NetworkService. The result is either returned through the onActivityResult call in the
     * provided Activity or through the NetworkServicePresenter.
     *
     * @param activity handles the payment that should be processed
     * @param requestCode should be returned to the presenter when the payment is processed
     * @param operation that should be processed
     */
    public void processPayment(Activity activity, int requestCode, Operation operation) {
    }

    /**
     * Notify the network service that the payment has been redirected and an OperationResult has been received.
     *
     * @param result containing the result of the operation
     */
    public void onRedirectSuccess(OperationResult result) {
    }

    /**
     * Notify the network service that the redirect has failed to receive an OperationResult.
     * The network service should handle this situation and make sure the NetworkServicePresenter is notified with the
     * appropriate PaymentResult.
     */
    public void onRedirectError() {
    }
}
