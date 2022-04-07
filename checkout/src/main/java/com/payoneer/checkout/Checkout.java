/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout;

import com.payoneer.checkout.localization.LocalLocalizationHolder;
import com.payoneer.checkout.localization.Localization;
import com.payoneer.checkout.ui.page.ChargePaymentActivity;
import com.payoneer.checkout.ui.screen.list.CheckoutListActivity;

import android.app.Activity;
import android.content.Intent;

/**
 * The Checkout class is the controller to initialize and launch the Payment List.
 * The Payment List shows payment methods which can be used to finalize payments through the Payment API.
 */
public final class Checkout {

    private final CheckoutConfiguration checkoutConfiguration;

    private Checkout(final CheckoutConfiguration checkoutConfiguration) {
        this.checkoutConfiguration = checkoutConfiguration;
    }

    /**
     * Create a new Checkout class from the provided CheckoutConfiguration
     *
     * @param checkoutConfiguration contains the listURL and theming
     * @return newly created Checkout Object
     */
    public static Checkout of(final CheckoutConfiguration checkoutConfiguration) {
        if (checkoutConfiguration == null) {
            throw new IllegalArgumentException("checkoutConfiguration cannot be null");
        }
        return new Checkout(checkoutConfiguration);
    }

    /**
     * Get the CheckoutConfiguration containing the listURL and theming configuration
     *
     * @return CheckoutConfiguration of this Checkout
     */
    public CheckoutConfiguration getCheckoutConfiguration() {
        return checkoutConfiguration;
    }

    /**
     * Charge the PresetAccount, if no PresetAccount is set in the ListResult then an error will be returned.
     *
     * @param activity the activity that will be notified with a CheckoutResult
     * @param requestCode the requestCode to be used for identifying results in the parent activity
     */
    public void chargePresetAccount(final Activity activity, final int requestCode) {
        Intent intent = ChargePaymentActivity.createStartIntent(activity, checkoutConfiguration);
        launchActivity(activity, intent, requestCode);
        activity.overridePendingTransition(ChargePaymentActivity.getStartTransition(), R.anim.no_animation);
    }

    /**
     * Show the PaymentList containing the list of supported payment methods.
     *
     * @param activity the activity that will be notified when the PaymentList is closed
     * @param requestCode the requestCode to be used for identifying results in the parent activity
     */
    public void showPaymentList(final Activity activity, final int requestCode) {
        Intent intent = CheckoutListActivity.createStartIntent(activity, checkoutConfiguration);
        launchActivity(activity, intent, requestCode);
        activity.overridePendingTransition(CheckoutListActivity.getStartTransition(), R.anim.no_animation);
    }

    /**
     * Validate Android SDK Settings and Localization before launching the Activity.
     *
     * @param activity the activity that will be notified when the Activity is finished
     * @param intent containing the session information
     * @param requestCode the requestCode to be used for identifying results in the parent activity
     */
    private void launchActivity(final Activity activity, final Intent intent, final int requestCode) {
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
