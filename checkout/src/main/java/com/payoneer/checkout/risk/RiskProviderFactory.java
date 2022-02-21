/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.risk;

/**
 * Interface for all RiskProviderFactories.
 * A RiskProviderFactory is capable of creating a RiskProvider instance for a third-party risk provider service with code and type.
 */
public interface RiskProviderFactory {

    /**
     * Check if this factory supports the third-party risk provider service with code and type
     *
     * @param riskProviderCode code of the risk provider
     * @param riskProviderType type of the risk provider
     * @return true when supported, false otherwise
     */
    boolean supports(String riskProviderCode, String riskProviderType);

    /**
     * Create a risk provider for the third-party risk provider service
     *
     * @return the newly created RiskProvider
     */
    RiskProvider createRiskProvider();
}
