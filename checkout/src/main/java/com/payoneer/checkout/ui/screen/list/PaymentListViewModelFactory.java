/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.screen.list;

import com.payoneer.checkout.account.DeleteAccountInteractor;
import com.payoneer.checkout.payment.PaymentServiceInteractor;
import com.payoneer.checkout.ui.session.PaymentSessionInteractor;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

/**
 * Factory for creating a new PaymentListViewModel
 */
final class PaymentListViewModelFactory implements ViewModelProvider.Factory {

    private final Context applicationContext;
    private final PaymentServiceInteractor serviceInteractor;
    private final PaymentSessionInteractor sessionInteractor;
    private final DeleteAccountInteractor accountInteractor;

    PaymentListViewModelFactory(@NonNull final Context applicationContext, @NonNull final PaymentSessionInteractor sessionInteractor,
        @NonNull PaymentServiceInteractor serviceInteractor, @NonNull final DeleteAccountInteractor accountInteractor) {
        this.applicationContext = applicationContext;
        this.sessionInteractor = sessionInteractor;
        this.serviceInteractor = serviceInteractor;
        this.accountInteractor = accountInteractor;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull final Class<T> modelClass) {
        return (T) new PaymentListViewModel(applicationContext, sessionInteractor, serviceInteractor, accountInteractor);
    }
}
