/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.dialog;

import com.payoneer.checkout.CheckoutActivityResult;
import com.payoneer.checkout.CheckoutResult;
import com.payoneer.checkout.localization.InteractionMessage;
import com.payoneer.checkout.ui.dialog.PaymentDialogFragment.PaymentDialogListener;

import android.app.Activity;

/**
 * Holding data for showing a specific dialog, e.g. hint or connection error.
 * The dialog listener will be informed of dialog events.
 */
public final class PaymentDialogData {

    public final static String CONNECTION_ERROR = "connection_error";
    public final static String CONFIRM_DELETE = "confirm_delete";
    public final static String CONFIRM_REFRESH = "confirm_refresh";
    public final static String INTERACTION = "interaction";
    public final static String HINT = "hint";
    public final static String EXPIRED = "expired";

    private final PaymentDialogListener listener;
    private final Object param0;
    private final Object param1;
    private final String type;

    private PaymentDialogData(final String type, final PaymentDialogListener listener, final Object param0, final Object param1) {
        this.type = type;
        this.listener = listener;
        this.param0 = param0;
        this.param1 = param1;
    }

    /**
     * Create connection error dialog data, notify the listener of events in this dialog.
     *
     * @param listener to be notified of dialog events
     */
    public static PaymentDialogData connectionErrorDialog(final PaymentDialogListener listener) {
        return new PaymentDialogData(CONNECTION_ERROR, listener, null, null);
    }

    /**
     * Create confirm delete dialog data, notify the listener of events in this dialog.
     *
     * @param listener to be notified of dialog events
     * @param accountLabel label of the account this is being deleted
     */
    public static PaymentDialogData confirmDeleteDialog(final PaymentDialogListener listener, final String accountLabel) {
        return new PaymentDialogData(CONFIRM_DELETE, listener, accountLabel, null);
    }

    /**
     * Create confirm refresh dialog, notify the listener of events in this dialog.
     *
     * @param listener to be notified of dialog events
     */
    public static PaymentDialogData confirmRefreshDialog(final PaymentDialogListener listener) {
        return new PaymentDialogData(CONFIRM_REFRESH, listener, null, null);
    }

    /**
     * Create the interaction dialog data, notify the listener of events in this dialog.
     * When there is no localization for the interaction then the default error will be shown to the user.
     *
     * @param interactionMessage used to show the proper localization message to the user
     * @param listener to be notified of dialog events
     */
    public static PaymentDialogData interactionDialog(final PaymentDialogListener listener, final InteractionMessage interactionMessage) {
        return new PaymentDialogData(INTERACTION, listener, null, null);
    }

    /**
     * Create the hint dialog data, notify the listener of events in this dialog
     *
     * @param listener listening to events of this hint Dialog.
     * @param networkCode Code if the network
     * @param type type if input field
     */
    public static PaymentDialogData hintDialog(final PaymentDialogListener listener, final String networkCode, final String type) {
        return new PaymentDialogData(HINT, listener, networkCode, type);
    }

    /**
     * Create expired dialog data, notify the listener of events in this dialog
     *
     * @param listener listening to events of this expired dialog
     * @param networkCode
     */
    public static PaymentDialogData expiredDialog(final PaymentDialogListener listener, final String networkCode) {
        return new PaymentDialogData(EXPIRED, listener, networkCode, null);
    }

    public PaymentDialogFragment.PaymentDialogListener getListener(){
        return listener;
    }

    public String getType() {
        return type;
    }

    public Object getParam0() {
        return param0;
    }

    public Object getParam1() {
        return param1;
    }
}

