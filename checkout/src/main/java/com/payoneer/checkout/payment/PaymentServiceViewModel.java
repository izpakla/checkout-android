/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.payment;

import com.payoneer.checkout.util.AppContextViewModel;
import com.payoneer.checkout.util.ContentEvent;
import com.payoneer.checkout.util.Event;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;

public final class PaymentServiceViewModel extends AppContextViewModel {

    private final PaymentServicePresenter presenter;
    public MutableLiveData<Event> finalizePayment;
    public MutableLiveData<ContentEvent> showFragment;

    PaymentServiceViewModel(final Context applicationContext, final PaymentServicePresenter presenter) {
        super(applicationContext);
        this.presenter = presenter;
        finalizePayment = new MutableLiveData<>();
        showFragment = new MutableLiveData<>();
        presenter.setPaymentServiceViewModel(this);
    }

    public void finalizePayment() {
        finalizePayment.setValue(new Event());
    }

    public void showFragment(final Fragment fragment) {
        showFragment.setValue(new ContentEvent(fragment));
    }
}
