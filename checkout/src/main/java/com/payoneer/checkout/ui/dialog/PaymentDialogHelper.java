/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.dialog;

import static com.payoneer.checkout.ui.dialog.PaymentDialogData.CONFIRM_DELETE;
import static com.payoneer.checkout.ui.dialog.PaymentDialogData.CONFIRM_REFRESH;
import static com.payoneer.checkout.ui.dialog.PaymentDialogData.CONNECTION_ERROR;
import static com.payoneer.checkout.ui.dialog.PaymentDialogData.EXPIRED;
import static com.payoneer.checkout.ui.dialog.PaymentDialogData.HINT;
import static com.payoneer.checkout.ui.dialog.PaymentDialogData.INTERACTION;

import com.payoneer.checkout.localization.InteractionMessage;
import com.payoneer.checkout.ui.dialog.PaymentDialogFragment.PaymentDialogListener;
import com.payoneer.checkout.ui.page.idlingresource.PaymentIdlingResources;

import androidx.fragment.app.FragmentManager;

/**
 * Helper class for showing different types of dialog fragments
 */
public class PaymentDialogHelper {

    private PaymentIdlingResources idlingResources;

    public PaymentDialogHelper(final PaymentIdlingResources idlingResources) {
        this.idlingResources = idlingResources;
    }

    public void showPaymentDialog(final FragmentManager fragmentManager, final PaymentDialogData data) {
        switch (data.getType()) {
            case CONNECTION_ERROR:
                showConnectionErrorDialog(fragmentManager, data.getListener());
                break;
            case CONFIRM_DELETE:
                showConfirmDeleteDialog(fragmentManager, (String) data.getParam0(), data.getListener());
                break;
            case CONFIRM_REFRESH:
                showConfirmRefreshDialog(fragmentManager, data.getListener());
                break;
            case INTERACTION:
                showInteractionDialog(fragmentManager, (InteractionMessage) data.getParam0(), data.getListener());
                break;
            case HINT:
                showHintDialog(fragmentManager, (String) data.getParam0(), (String) data.getParam1(), data.getListener());
                break;
            case EXPIRED:
                showExpiredDialog(fragmentManager, (String) data.getParam0(), data.getListener());
                break;
            default:
                throw new IllegalArgumentException("Unknown payment dialog fragment: " + data.getType());
        }
    }

    public void showConnectionErrorDialog(final FragmentManager fragmentManager, final PaymentDialogListener listener) {
        PaymentDialogFragment paymentDialog = PaymentDialogFactory.createConnectionErrorDialog(listener);
        showPaymentDialog(fragmentManager, paymentDialog);
    }

    public void showConfirmDeleteDialog(final FragmentManager fragmentManager, final String account, final PaymentDialogListener listener) {
        PaymentDialogFragment paymentDialog = PaymentDialogFactory.createConfirmDeleteDialog(listener, account);
        showPaymentDialog(fragmentManager, paymentDialog);
    }

    public void showConfirmRefreshDialog(final FragmentManager fragmentManager, final PaymentDialogListener listener) {
        PaymentDialogFragment paymentDialog = PaymentDialogFactory.createConfirmRefreshDialog(listener);
        showPaymentDialog(fragmentManager, paymentDialog);
    }

    public void showInteractionDialog(final FragmentManager fragmentManager, final InteractionMessage message,
        final PaymentDialogListener listener) {
        PaymentDialogFragment paymentDialog = PaymentDialogFactory.createInteractionDialog(message, listener);
        showPaymentDialog(fragmentManager, paymentDialog);
    }

    public void showHintDialog(final FragmentManager fragmentManager, final String networkCode, final String type,
        final PaymentDialogListener listener) {
        PaymentDialogFragment dialog = PaymentDialogFactory.createHintDialog(networkCode, type, listener);
        showPaymentDialog(fragmentManager, dialog);
    }

    public void showExpiredDialog(final FragmentManager fragmentManager, final String networkCode, final PaymentDialogListener listener) {
        PaymentDialogFragment dialog = PaymentDialogFactory.createExpiredDialog(networkCode, listener);
        showPaymentDialog(fragmentManager, dialog);
    }

    public void showPaymentDialog(final FragmentManager fragmentManager, final PaymentDialogFragment dialogFragment) {
        dialogFragment.showDialog(fragmentManager, idlingResources);
    }
}
