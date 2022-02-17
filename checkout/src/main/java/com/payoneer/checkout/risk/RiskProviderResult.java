/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.risk;

import java.util.HashMap;
import java.util.Map;

/**
 * RiskProviderResult containing the collected risk result data provided by the external risk service
 */
public final class RiskProviderResult {
    private final Map<String, String> riskData;

    public RiskProviderResult() {
        this.riskData = new HashMap<>();
    }

    public void put(String key, String value) {
        riskData.put(key, value);
    }

    public Map<String, String> getRiskData() {
        return riskData;
    }
}
