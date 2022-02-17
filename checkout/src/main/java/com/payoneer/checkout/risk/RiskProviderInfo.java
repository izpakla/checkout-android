/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.risk;

import java.util.Map;

/**
 * RiskProviderInfo containing the information about the risk provider
 */
public final class RiskProviderInfo {

    private final String riskProviderCode;
    private final String riskProviderType;
    private final Map<String, String> parameters;

    /**
     * Create a new instance of the RiskProviderInfo object
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
}
