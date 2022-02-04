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
 * Fallback risk provider factory.
 * This risk provider factory creates a risk provider that handles the response for providers that are not supported/loaded into the Checkout SDK
 */
public final class FallbackRiskProviderFactory implements RiskProviderFactory {

    /**
     * Check if this provider supports the provided providerCode and type
     *
     * @param providerCode code of the risk provider
     * @param providerType type of the risk provider
     * @return true when supported, false otherwise
     */
    public boolean supports(String providerCode, String providerType) {
        return true;
    }

    /**
     * Create a new risk provider
     *
     * @param context context in which this service will run
     * @return the newly created risk provider
     */
    public RiskProvider createRiskProvider(Context context) {
        return new FallbackRiskProvider();
    }
}
