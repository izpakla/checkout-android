/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.util;

import static com.payoneer.checkout.model.PaymentMethod.CREDIT_CARD;
import static com.payoneer.checkout.model.PaymentMethod.DEBIT_CARD;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.payoneer.checkout.core.PaymentInputType;
import com.payoneer.checkout.model.AccountMask;
import com.payoneer.checkout.model.InputElement;
import com.payoneer.checkout.model.Parameter;

import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

/**
 * The PaymentUtils class containing helper methods
 */
public final class AccountMaskUtils {

    private final static String DOTS = " •••• ";

    /**
     * Get a correctly formatted label from the AccountMask depending if the AccountMask is part of a
     * debit/credit card, IBAN or other payment method.
     *
     * @param accountMask containing the label information
     * @param paymentMethod to which this accountMask belongs to
     * @param networkLabel the label of the network
     * @return the label for this AccountMask
     */
    public static String getAccountMaskLabel(AccountMask accountMask, String paymentMethod, String networkLabel) {
        if (PaymentUtils.isCardPaymentMethod(paymentMethod)) {
            return formatCardAccountMaskLabel(accountMask.getNumber(), networkLabel);
        } else if (!TextUtils.isEmpty(accountMask.getIban())) {
            return formatIbanAccountMaskLabel(accountMask.getIban());
        }
        return accountMask.getDisplayLabel();
    }

    /**
     * Format the card number replacing the *** characters with ••••
     *
     * @param number representing the original AccountMask card number
     * @param networkLabel label of the payment network
     * @return formatted card number label
     */
    public static String formatCardAccountMaskLabel(String number, String networkLabel) {
        if (number == null || networkLabel == null) {
            return "";
        }
        int lastStarIndex = number.lastIndexOf('*');
        if (lastStarIndex == -1) {
            return networkLabel + " " + number;
        } else {
            String postfix = number.substring(lastStarIndex + 1).trim();
            return networkLabel + DOTS + postfix;
        }
    }

    /**
     * Format the iban number from the AccountMask
     *
     * @param iban representing the original AccountMask iban number
     * @return formatted iban number
     */
    public static String formatIbanAccountMaskLabel(String iban) {
        if (iban == null) {
            return "";
        }
        int lastStarIndex = iban.lastIndexOf('*');
        if (lastStarIndex == -1) {
            return iban;
        }
        String prefix = iban.substring(0, Math.min(4, iban.length())).trim();
        String postfix = iban.substring(lastStarIndex + 1).trim();
        return prefix + DOTS + postfix;
    }

    /**
     * Create am expiry date string from the AccountMask.
     * If the AccountMask does not contain the expiryMonth and expiryYear values then return null.
     *
     * @param mask AccountMask containing the expiryMonth and expiryYear fields
     * @return the expiry date or null if it could not be created
     */
    public static String getExpiryDateString(AccountMask mask) {
        int month = PaymentUtils.toInt(mask.getExpiryMonth());
        int year = PaymentUtils.toInt(mask.getExpiryYear());
        if (month == 0 || year == 0) {
            return null;
        }
        return PaymentUtils.format("%1$02d / %2$d", month, (year % 100));
    }
}
