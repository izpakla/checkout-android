/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.core;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.StringDef;

/**
 * Class containing the different types of links
 */
public class PaymentLinkType {

    public final static String SELF = "self";
    public final static String ONSELECT = "onSelect";
    public final static String LOGO = "logo";
    public final static String OPERATION = "operation";
    public final static String LANGUAGE = "lang";

    /**
     * Check if the given type is a valid payment link
     *
     * @param type the payment input type to validate
     * @return true when valid, false otherwise
     */
    public static boolean isValid(final String type) {

        if (type != null) {
            switch (type) {
                case SELF:
                case ONSELECT:
                case LOGO:
                case OPERATION:
                case LANGUAGE:
                    return true;
            }
        }
        return false;
    }

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
        SELF,
        ONSELECT,
        LOGO,
        OPERATION,
        LANGUAGE
    })
    public @interface Definition { }
}
