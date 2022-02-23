/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.risk;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.payoneer.checkout.model.Parameter;

/**
 * RiskProviderResult contains the risk result data provided by the third-party risk provider service.
 */
public final class RiskProviderResult {
    private final Map<String, String> riskData;

    public RiskProviderResult() {
        this.riskData = new HashMap<>();
    }

    public void put(final String key, final String value) {
        riskData.put(key, value);
    }

    public Map<String, String> getRiskData() {
        return riskData;
    }

    /**
     * Copy the risk result data into the list or parameters
     *
     * @param parameters list of parameters into which the risk result data should be copied to
     */
    public void copyInto(final List<Parameter> parameters) {
        if (parameters == null) {
            throw new IllegalArgumentException("Parameters cannot be null");
        }
        for (Map.Entry<String, String> entry : riskData.entrySet()) {
            Parameter param = new Parameter();
            param.setName(entry.getKey());
            param.setValue(entry.getValue());
            parameters.add(param);
        }
    }
}
