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
    public MutableLiveData<Event> processPaymentActive;
    public MutableLiveData<Event> processPaymentFinished;
    public MutableLiveData<Event> deleteAccountActive;
    public MutableLiveData<Event> deleteAccountFinished;
    public MutableLiveData<ContentEvent> showFragment;

    PaymentServiceViewModel(final Context applicationContext, final PaymentServicePresenter presenter) {
        super(applicationContext);
        this.presenter = presenter;

        showFragment = new MutableLiveData<>();
        processPaymentActive = new MutableLiveData<>();
        processPaymentFinished = new MutableLiveData<>();
        deleteAccountActive = new MutableLiveData<>();
        deleteAccountFinished = new MutableLiveData<>();
        presenter.setPaymentServiceViewModel(this);
    }

    public void processPaymentActive() {
        processPaymentActive.setValue(new Event());
    }

    public void processPaymentFinished() {
        processPaymentFinished.setValue(new Event());
    }

    public void deleteAccountActive() {
        deleteAccountActive.setValue(new Event());
    }

    public void deleteAccountFinished() {
        deleteAccountFinished.setValue(new Event());
    }

    public void showFragment(final Fragment fragment) {
        showFragment.setValue(new ContentEvent(fragment));
    }
}
