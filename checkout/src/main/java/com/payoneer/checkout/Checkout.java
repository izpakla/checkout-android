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

    /**
     * Create a new Checkout class with the mandatory listUrl
     *
     * @param builder contains the CheckoutInfo builder
     */
    private Checkout(final CheckoutInfo.Builder builder) {
        this.builder = builder;
    }

    /**
     * Create a new Checkout class with the listUrl
     *
     * @param listUrl contains the Url string of the payment list
     * @return newly created new Checkout Object
     */
    public static Checkout with(final String listUrl) {
        CheckoutInfo.Builder builder = new CheckoutInfo.Builder(listUrl);
        return new Checkout(builder);
    }

    /**
     * Create a new Checkout class with the CheckoutInfo object containing
     * the information about the Checkout
     *
     * @param info contains the listUrl and optional theming and orientation
     * @return newly created new Checkout Object
     */
    public static Checkout with(final CheckoutInfo info) {
        CheckoutInfo.Builder builder = new CheckoutInfo.Builder(info);
        return new Checkout(builder);
    }

    /**
     * Set the orientation in this Checkout Object
     *
     * @param orientation
     * @return this Checkout Object
     */
    public Checkout orientation(final int orientation) {
        builder.setOrientation(orientation);
        return this;
    }

    /**
     * SEt the theme in this Checkout Object
     *
     * @param theme to be set
     * @return this Checkout Object
     */
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
    public CheckoutInfo chargePresetAccount(final Activity activity, final int requestCode) {
        CheckoutInfo info = builder.build();
        Intent intent = ChargePaymentActivity.createStartIntent(activity, info);
        launchActivity(activity, intent, requestCode);
        activity.overridePendingTransition(ChargePaymentActivity.getStartTransition(), R.anim.no_animation);
    }

    /**
     * Open the PaymentPage containing the list of supported payment methods.
     *
     * @param activity the activity that will be notified when this PaymentPage is finished
     * @param requestCode the requestCode to be used for identifying results in the parent activity
     */
    public CheckoutInfo showPaymentList(final Activity activity, final int requestCode) {
        CheckoutInfo info = builder.build();
        Intent intent = PaymentListActivity.createStartIntent(activity, info);
        launchActivity(activity, intent, requestCode);
        activity.overridePendingTransition(PaymentListActivity.getStartTransition(), R.anim.no_animation);
        return info;
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
