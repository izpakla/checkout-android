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
 * This class is basically an extension of {@link com.payoneer.checkout.risk.RiskProviderResult} with a map
 * of the external and internal errors to send back to OPG as part of the result object
 */
public class RiskErrors {

    public static final String RESULTKEY_EXTERNAL_ERROR = "riskPluginExternalError";
    public static final String RESULTKEY_INTERNAL_ERROR = "riskPluginInternalError";
    private final Map<String, String> riskErrors = new HashMap<>();

    public void put(final String key, final String value) {
        riskErrors.put(key, value);
    }

    public Map<String, String> getRiskErrors() {
        return riskErrors;
    }
}
