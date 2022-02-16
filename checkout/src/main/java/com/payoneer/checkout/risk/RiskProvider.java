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
 * Interface for a risk provider, a RiskService is responsible for activating and
 * obtaining risk data from a third-party risk provider service
 */
public interface RiskProvider {

    /**
     * Initialize this RiskProvider with the provider risk information
     *
     * @param context into which this risk provider will be loaded
     * @param info contains information about how the risk provider
     */
    void initialize(Context context, RiskProviderInfo info) throws RiskException;

    /**
     * Get the risk result from this risk provider
     *
     * @return the risk result obtained by this risk provider
     */
    RiskProviderResult getRiskProviderResult() throws RiskException;
}
