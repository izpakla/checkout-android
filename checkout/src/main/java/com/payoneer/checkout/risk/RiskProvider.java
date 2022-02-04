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
 * Interface for a risk provider, a RiskService is responsible for activating and
 * obtaining risk data from a third-party risk provider service
 */
public interface RiskProvider {

    /**
     * Initialize this RiskProvider with the provider risk parameters
     * @param providerParameters used to initialize this risk provider
     */
    public void initialize(ProviderParameters providerParameters);

    /**
     * Get the risk data from this risk provider
     *
     * @return the risk data obtained by this risk provider
     */
    public ProviderParameters getRiskData();
}
