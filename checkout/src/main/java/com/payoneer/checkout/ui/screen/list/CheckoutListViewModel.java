/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.screen.list;

import com.payoneer.checkout.ui.model.PaymentSession;
import com.payoneer.checkout.util.AppContextViewModel;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

class CheckoutListViewModel extends AppContextViewModel {

    private final CheckoutListPresenter presenter;
    MutableLiveData<PaymentSession> paymentSession;

    public CheckoutListViewModel(final Context applicationContext, final CheckoutListPresenter presenter) {
        super(applicationContext);
        this.presenter = presenter;
        this.paymentSession = new MutableLiveData<>();
        this.presenter.setViewModel(this);
    }

    public LiveData<PaymentSession> getPaymentSession() {
        return paymentSession;
    }

    public void loadPaymentSession() {
        presenter.loadPaymentSession();
    }

    public void setPaymentSession(final PaymentSession paymentSession) {
        this.paymentSession.setValue(paymentSession);
    }
}
