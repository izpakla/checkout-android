/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.payment;

import com.payoneer.checkout.util.AppContextViewModel;

import android.content.Context;
import android.os.Bundle;

/**
 * PaymentServiceViewModel provides communication between the view and the payment service.
 */
public final class PaymentServiceViewModel extends AppContextViewModel {

    private final PaymentServiceInteractor serviceInteractor;

    public PaymentServiceViewModel(final Context applicationContext, final PaymentServiceInteractor serviceInteractor) {
        super(applicationContext);
        this.serviceInteractor = serviceInteractor;
    }

    public void onFragmentResult(final Bundle fragmentResult) {
        serviceInteractor.onFragmentResult(fragmentResult);
    }
}
