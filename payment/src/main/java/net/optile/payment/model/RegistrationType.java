/*
 * Copyright(c) 2012-2018 optile GmbH. All Rights Reserved.
 * https://www.optile.net
 *
 * This software is the property of optile GmbH. Distribution  of  this
 * software without agreement in writing is strictly prohibited.
 *
 * This software may not be copied, used or distributed unless agreement
 * has been received in full.
 */

package net.optile.payment.model;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import android.support.annotation.StringDef;

/**
 * This class describes registration type behavior for applicable network.
 */
public class RegistrationType {

    public final static String NONE = "NONE";
    public final static String OPTIONAL = "OPTIONAL";
    public final static String FORCED = "FORCED";
    public final static String OPTIONAL_PRESELECTED = "OPTIONAL_PRESELECTED";
    public final static String FORCED_DISPLAYED = "FORCED_DISPLAYED";

    /**
     * Check if the given type is a valid registration type
     *
     * @param type the registration type to validate
     * @return true when valid, false otherwise
     */
    public static boolean isRegistrationType(final String type) {

        if (type != null) {
            switch (type) {
                case NONE:
                case OPTIONAL:
                case FORCED:
                case OPTIONAL_PRESELECTED:
                case FORCED_DISPLAYED:
                    return true;
            }
        }
        return false;
    }

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
        NONE,
        OPTIONAL,
        FORCED,
        OPTIONAL_PRESELECTED,
        FORCED_DISPLAYED })
    public @interface Definition { }
}



