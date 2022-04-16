/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.screen.payment;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

/**
 * Class for observing the lifecycle of the ProcessPaymentActivity.
 * Once the ProcessPaymentActivity is resumed, the presenter is informed that it could
 * resume processing of a pending payment, e.g. after a redirect.
 */
final class ProcessPaymentObserver implements LifecycleEventObserver {

    private final ProcessPaymentPresenter presenter;

    ProcessPaymentObserver(final ProcessPaymentPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onStateChanged(@NonNull final LifecycleOwner source, @NonNull final Lifecycle.Event event) {
        switch (event) {
            case ON_RESUME:
                presenter.onProcessPaymentResume();
                break;
            case ON_PAUSE:
                presenter.onProcessPaymentPause();
                break;
        }
    }
}
