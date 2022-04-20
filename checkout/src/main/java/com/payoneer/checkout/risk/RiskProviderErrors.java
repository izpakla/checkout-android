/*
 *
 *   Copyright (c) 2022 Payoneer Germany GmbH
 *   https://www.payoneer.com
 *
 *   This file is open source and available under the MIT license.
 *   See the LICENSE file for more information.
 *
 */

package com.payoneer.checkout.risk;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to store internal and external errors generated by a risk provider.
 */
public final class RiskProviderErrors {

    private static final String RESULTKEY_EXTERNAL_ERROR = "riskPluginExternalError";
    private static final String RESULTKEY_INTERNAL_ERROR = "riskPluginInternalError";
    private static final int MAX_ERROR_LENGTH = 2000;

    private final Map<String, String> errors;

    public RiskProviderErrors() {
        this.errors = new HashMap<>();
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public boolean isEmpty() {
        return errors.isEmpty();
    }

    public void putInternalError(final String error) {
        errors.put(RESULTKEY_INTERNAL_ERROR, trimError(error));
    }

    public void putExternalError(final String error) {
        errors.put(RESULTKEY_EXTERNAL_ERROR, trimError(error));
    }

    private String trimError(final String error) {
        if (error.length() > MAX_ERROR_LENGTH) {
            return error.substring(0, MAX_ERROR_LENGTH);
        } else {
            return error;
        }
    }
}
