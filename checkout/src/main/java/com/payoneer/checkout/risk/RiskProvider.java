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
 * Interface for a risk provider. A risk provider implementation provides risk data collected by a third-party risk provider.
 */
public interface RiskProvider {

    /**
     * Initialize this RiskProvider with the provider risk information
     *
     * @param info contains information how to initialize this risk provider
     * @param applicationContext may be used to initialize this risk provider
     * @throws RiskException when an error occurred while initializing this risk provider
     */
    void initialize(RiskProviderInfo info, Context applicationContext) throws RiskException;

    /**
     * Get the risk data result from this risk provider
     *
     * @param applicationContext may be used to create the provider result data
     * @return the risk result obtained by this risk provider
     * @throws RiskException when an error occurred while collecting risk data result
     */
    RiskProviderResult getRiskProviderResult(Context applicationContext) throws RiskException;
}
