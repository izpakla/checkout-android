/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.risk;

import com.payoneer.checkout.model.ProviderParameters;

/**
 * Fallback risk provider handling risk providers that were not enabled in the Checkout SDK
 */
public final class FallbackRiskProvider implements RiskProvider {

    private ProviderParameters parameters;

    @Override
    public void initialize(ProviderParameters providerParameters) {
    }

    @Override
    public void refresh() {
    }

    @Override
    public ProviderParameters getRiskData() {
        ProviderParameters result = new ProviderParameters();
        result.setProviderCode(parameters.getProviderCode());
        result.setProviderType(parameters.getProviderType());
        return result;
    }
}
