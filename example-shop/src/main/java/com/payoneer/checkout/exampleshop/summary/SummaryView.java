/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.exampleshop.summary;

import com.payoneer.checkout.model.PresetAccount;

import android.content.Context;

/**
 * The interface for the SummaryView.
 */
interface SummaryView {

    /**
     * Close this summary view
     */
    void close();

    /**
     * Show the payment confirmation to the user
     */
    void showPaymentConfirmation();

    /**
     * Show the list of payments using the android-sdk
     */
    void showPaymentList();

    /**
     * Abort the payment and show a default error to the user
     */
    void stopPaymentWithErrorMessage();

    /**
     * Show the loading animation
     *
     * @param val true when the loading animation should be shown, false otherwise
     */
    void showLoading(boolean val);

    /**
     * Show preset account
     *
     * @param account to be shown in the summary page
     */
    void showPaymentDetails(PresetAccount account);

    /**
     * Get the Context in which this presenter is operating
     *
     * @return the context
     */
    Context getContext();

    /**
     * Get the list url from the summary view
     *
     * @return the list url
     */
    String getListUrl();
}


