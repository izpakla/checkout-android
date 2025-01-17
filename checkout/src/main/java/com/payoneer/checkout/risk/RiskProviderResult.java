/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.risk;

import java.util.ArrayList;
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

    public RiskProviderResult(final Map<String, String> riskData) {
        this.riskData = riskData;
    }

    public void put(final String key, final String value) {
        riskData.put(key, value);
    }

    public Map<String, String> getRiskData() {
        return riskData;
    }

    public static RiskProviderResult of(final RiskProviderErrors riskProviderErrors) {
        return new RiskProviderResult(new HashMap<>(riskProviderErrors.getErrors()));
    }

    /**
     * Get the risk provider result as a list of parameters.
     *
     * @return list of parameters containing the provider result
     */
    public List<Parameter> getProviderResultParameters() {
        List<Parameter> parameters = new ArrayList<>();
        for (Map.Entry<String, String> entry : riskData.entrySet()) {
            Parameter param = new Parameter();
            param.setName(entry.getKey());
            param.setValue(entry.getValue());
            parameters.add(param);
        }
        return parameters;
    }
}
