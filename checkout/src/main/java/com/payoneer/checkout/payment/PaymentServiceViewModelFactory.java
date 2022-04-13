/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.payment;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class PaymentServiceViewModelFactory implements ViewModelProvider.Factory {

    private final Context applicationContext;
    private final PaymentServicePresenter presenter;

    public PaymentServiceViewModelFactory(@NonNull final Context applicationContext, @NonNull final PaymentServicePresenter presenter) {
        this.applicationContext = applicationContext;
        this.presenter = presenter;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull final Class<T> modelClass) {
        return (T) new PaymentServiceViewModel(applicationContext, presenter);
    }
}
