/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout;

import com.payoneer.checkout.localization.LocalLocalizationHolder;
import com.payoneer.checkout.localization.Localization;
import com.payoneer.checkout.ui.page.ChargePaymentActivity;
import com.payoneer.checkout.ui.page.PaymentListActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.text.TextUtils;
import android.util.Patterns;

/**
 * The Checkout class is the controller to initialize and launch the Payment List.
 * The Payment List shows payment methods which can be used to finalize payments through the Payment API.
 */
public final class Checkout {

    private CheckoutInfo.Builder builder;

    private Checkout(final String listUrl) {
        builder = CheckoutInfo.createBuilder(listUrl);
    }

    public static Checkout with(final String listUrl) {
        return new Checkout(listUrl);
    }

    public static Checkout with(final CheckoutInfo info) {
        return new Checkout(info.getListUrl()).orientation(info.getOrientation()).theme(info.getTheme()));
    }

    public Checkout orientation(final int orientation) {
        builder.setOrientation(orientation);
        return this;
    }

    public Checkout theme(final CheckoutTheme theme) {
        builder.setTheme(theme);
        return this;
    }

    /**
     * Open the PaymentPage and instruct the page to immediately charge the PresetAccount.
     * If no PresetAccount is set in the ListResult then an error will be returned.
     *
     * @param activity the activity that will be notified when this PaymentPage is finished
     * @param requestCode the requestCode to be used for identifying results in the parent activity
     */
    public static void chargePresetAccount(final Activity activity, final int requestCode) {
        Intent intent = ChargePaymentActivity.createStartIntent(activity, builder.build());
        launchActivity(activity, intent, requestCode);
        activity.overridePendingTransition(ChargePaymentActivity.getStartTransition(), R.anim.no_animation);
    }

    /**
     * Open the PaymentPage containing the list of supported payment methods.
     *
     * @param activity the activity that will be notified when this PaymentPage is finished
     * @param requestCode the requestCode to be used for identifying results in the parent activity
     */
    public void showPaymentList(final Activity activity, final int requestCode) {
        Intent intent = PaymentListActivity.createStartIntent(activity, builder.build());
        launchActivity(activity, intent, requestCode);
        activity.overridePendingTransition(PaymentListActivity.getStartTransition(), R.anim.no_animation);
    }

    /**
     * Validate Android SDK Settings and Localization before launching the Activity.
     *
     * @param activity the activity that will be notified when this PaymentPage is finished
     * @param intent containing the session information
     * @param requestCode the requestCode to be used for identifying results in the parent activity
     */
    private void launchActivity(Activity activity, Intent intent, int requestCode) {
        if (activity == null) {
            throw new IllegalArgumentException("activity may not be null");
        }
        if (intent == null) {
            throw new IllegalArgumentException("intent may not be null");
        }
        initLocalization(activity);

        activity.finishActivity(requestCode);
        activity.startActivityForResult(intent, requestCode);
    }

    private void initLocalization(Activity activity) {
        Localization loc = new Localization(new LocalLocalizationHolder(activity), null);
        Localization.setInstance(loc);
    }
}
