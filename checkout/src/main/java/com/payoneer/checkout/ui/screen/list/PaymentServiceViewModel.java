/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.screen.list;

import com.payoneer.checkout.CheckoutResult;
import com.payoneer.checkout.localization.InteractionMessage;
import com.payoneer.checkout.payment.PaymentInputValues;
import com.payoneer.checkout.ui.dialog.PaymentDialogData;
import com.payoneer.checkout.ui.dialog.PaymentDialogFragment.PaymentDialogListener;
import com.payoneer.checkout.ui.model.PaymentCard;
import com.payoneer.checkout.ui.model.PaymentSession;
import com.payoneer.checkout.util.AppContextViewModel;
import com.payoneer.checkout.util.ContentEvent;
import com.payoneer.checkout.util.Event;
import com.payoneer.checkout.util.Resource;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;

final class PaymentServiceViewModel extends AppContextViewModel {

    private final PaymentServicePresenter presenter;
    MutableLiveData<Event> showProgress;

    PaymentServiceViewModel(final Context applicationContext, final PaymentServicePresenter presenter) {
        super(applicationContext);
        this.presenter = presenter;

        this.presenter.setPaymentServiceViewModel(this);
    }
}
