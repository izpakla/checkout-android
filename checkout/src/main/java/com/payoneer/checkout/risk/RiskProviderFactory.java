/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.risk;

import android.content.Context;

/**
 * Interface for all risk provider factories. A risk provider factory is capable of creating a NetworkService instance for a specific PaymentNetwork type.
 */
public interface RiskProviderFactory {

    /**
     * Check if this provider supports the provided providerCode and type
     *
     * @param providerCode code of the risk provider
     * @param providerType type of the risk provider
     * @return true when supported, false otherwise
     */
    boolean supports(String providerCode, String providerType);

    /**
     * Create a risk provider for this specific risk service
     *
     * @param context context in which this service will run
     * @return the newly created risk provider
     */
    RiskProvider createRiskProvider(Context context);
}
