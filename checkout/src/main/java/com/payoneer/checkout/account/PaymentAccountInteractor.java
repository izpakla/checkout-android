/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.account;

import com.payoneer.checkout.CheckoutResult;
import com.payoneer.checkout.CheckoutResultHelper;
import com.payoneer.checkout.model.OperationResult;

import android.content.Context;

/**
 * Class for handling the interaction between the PaymentService and e.g. a ViewModel.
 */
public final class PaymentAccountInteractor {

    private final PaymentAccountService accountService;
    private Observer observer;

    public PaymentAccountInteractor() {
        accountService = new PaymentAccountService();
        accountService.setListener(new PaymentAccountListener() {
            @Override
            public void onDeleteAccountSuccess(final OperationResult operationResult) {
                if (observer != null) {
                    observer.onDeleteAccountResult(new CheckoutResult(operationResult));
                }
            }

            @Override
            public void onDeleteAccountError(final Throwable cause) {
                if (observer != null) {
                    observer.onDeleteAccountResult(CheckoutResultHelper.fromThrowable(cause));
                }
            }
        });
    }

    public void onStop() {
        accountService.stop();
    }

    public void setObserver(final PaymentAccountInteractor.Observer observer) {
        this.observer = observer;
    }

    public void deleteAccount(final DeleteAccount account, final Context applicationContext) {
        if (!accountService.isActive()) {
            accountService.deleteAccount(account, applicationContext);
        }
    }

    /**
     * Observer interface for listening to events from this PaymentAccount interactor.
     */
    public interface Observer {
        void onDeleteAccountResult(final CheckoutResult checkoutResult);
    }
}
