/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.screen.list;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

/**
 * Class for observing the lifecycle of the PaymentListActivity.
 * Once the PaymentListActivity is resumed, the presenter is informed that it could
 * resume processing of a pending payment e.g. after a redirect.
 */
final class PaymentListObserver implements LifecycleEventObserver {

    private final PaymentListPresenter presenter;

    PaymentListObserver(final PaymentListPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onStateChanged(@NonNull final LifecycleOwner source, @NonNull final Lifecycle.Event event) {
        switch (event) {
            case ON_RESUME:
                presenter.onCheckoutListResume();
                break;
            case ON_PAUSE:
                presenter.onCheckoutListPause();
                break;
        }
    }
}
