/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.risk;

/**
 * Interface for all risk provider factories.
 * A risk provider factory is capable of creating a Risk Provider instance for a specific risk provider with code and type.
 */
public interface RiskProviderFactory {

    /**
     * Check if this factory supports the risk provider with code and type
     *
     * @param riskProviderCode code of the risk provider
     * @param riskProviderType type of the risk provider
     * @return true when supported, false otherwise
     */
    boolean supports(String riskProviderCode, String riskProviderType);

    /**
     * Create a risk provider for this specific external risk service
     *
     * @return the newly created risk provider
     */
    RiskProvider createRiskProvider();
}
