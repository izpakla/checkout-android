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
import java.util.Objects;

import com.payoneer.checkout.model.Parameter;
import com.payoneer.checkout.model.ProviderParameters;

/**
 * RiskProviderInfo containing information about the third-party risk provider service.
 */
public final class RiskProviderInfo {

    private final String riskProviderCode;
    private final String riskProviderType;
    private final Map<String, String> parameters;

    /**
     * Create a new instance of the RiskProviderInfo
     *
     * @param riskProviderCode code of the risk provider
     * @param riskProviderType type of the risk provider
     * @param parameters contains parameters used to initialize the risk provider
     */
    public RiskProviderInfo(final String riskProviderCode, final String riskProviderType, Map<String, String> parameters) {
        this.riskProviderCode = riskProviderCode;
        this.riskProviderType = riskProviderType;
        this.parameters = parameters;
    }

    public String getRiskProviderCode() {
        return riskProviderCode;
    }

    public String getRiskProviderType() {
        return riskProviderType;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    /**
     * Create a new RiskProviderInfo from the provided ProviderParameters
     *
     * @param providerParameters to be converted into a RiskProviderInfo
     * @return newly created RiskProviderInfo
     */
    public static RiskProviderInfo fromProviderParameters(final ProviderParameters providerParameters) {
        Objects.requireNonNull(providerParameters);

        String providerCode = providerParameters.getProviderCode();
        String providerType = providerParameters.getProviderType();
        Map<String, String> map = new HashMap<>();

        List<Parameter> parameters = providerParameters.getParameters();
        if (parameters != null) {
            for (Parameter parameter : parameters) {
                map.put(parameter.getName(), parameter.getValue());
            }
        }
        return new RiskProviderInfo(providerCode, providerType, map);
    }
}
