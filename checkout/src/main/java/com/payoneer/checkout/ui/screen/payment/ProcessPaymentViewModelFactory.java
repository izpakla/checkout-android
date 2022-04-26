/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.screen.payment;

import com.payoneer.checkout.payment.PaymentServiceInteractor;
import com.payoneer.checkout.ui.session.PaymentSessionInteractor;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

/**
 * Factory class for creating the ProcessPaymentViewModel
 */
final class ProcessPaymentViewModelFactory implements ViewModelProvider.Factory {

    private final Context applicationContext;
    private final PaymentServiceInteractor serviceInteractor;
    private final PaymentSessionInteractor sessionInteractor;

    ProcessPaymentViewModelFactory(@NonNull final Context applicationContext, @NonNull final PaymentSessionInteractor sessionInteractor,
        @NonNull
            PaymentServiceInteractor serviceInteractor) {
        this.applicationContext = applicationContext;
        this.sessionInteractor = sessionInteractor;
        this.serviceInteractor = serviceInteractor;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull final Class<T> modelClass) {
        return (T) new ProcessPaymentViewModel(applicationContext, sessionInteractor, serviceInteractor);
    }
}
