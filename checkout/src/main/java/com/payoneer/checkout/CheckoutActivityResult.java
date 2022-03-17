/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout;

import android.app.Activity;
import android.content.Intent;

/**
 * A container holding the CheckoutResult provided through the onActivityResult callback method
 */
public final class CheckoutActivityResult {

    public final static int RESULT_CODE_PROCEED = Activity.RESULT_FIRST_USER;
    public final static int RESULT_CODE_ERROR = Activity.RESULT_FIRST_USER + 1;

    private final int requestCode;
    private final int resultCode;
    private final CheckoutResult checkoutResult;

    /**
     * Construct a new CheckoutActivityResult Object
     *
     * @param requestCode activity requestCode
     * @param resultCode activity resultCode
     * @param checkoutResult containing the result of the checkout request
     */
    public CheckoutActivityResult(final int requestCode, final int resultCode, final CheckoutResult checkoutResult) {
        this.requestCode = requestCode;
        this.resultCode = resultCode;
        this.checkoutResult = checkoutResult;
    }

    /**
     * Construct a new CheckoutActivityResult Object
     *
     * @param requestCode activity requestCode
     * @param resultCode activity resultCode
     * @param data containing the activity result data
     */
    public static CheckoutActivityResult fromActivityResult(final int requestCode, final int resultCode, final Intent data) {
        CheckoutResult result = CheckoutResultHelper.fromResultIntent(data);
        return new CheckoutActivityResult(requestCode, resultCode, result);
    }

    /**
     * Get a string representation of the resultCode
     *
     * @return the String representation of the resultCode
     */
    public static String resultCodeToString(final int resultCode) {
        switch (resultCode) {
            case RESULT_CODE_PROCEED:
                return "RESULT_CODE_PROCEED";
            case RESULT_CODE_ERROR:
                return "RESULT_CODE_ERROR";
            case Activity.RESULT_CANCELED:
                return "Activity.RESULT_CANCELED";
            default:
                return "Unknown";
        }
    }

    public int getRequestCode() {
        return requestCode;
    }

    public int getResultCode() {
        return resultCode;
    }

    public CheckoutResult getCheckoutResult() {
        return checkoutResult;
    }
}
