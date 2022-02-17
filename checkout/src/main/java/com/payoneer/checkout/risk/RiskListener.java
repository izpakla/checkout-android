/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.risk;

import java.util.List;

import com.payoneer.checkout.model.ProviderParameters;

/**
 * Listener to be called by the RiskService to inform about risk operation updates.
 */
public interface RiskListener {

    /**
     * Called when the risk providers have been initialized.
     */
    void onRiskInitializedSuccess();

    /**
     * Called when an error occurred while initializing the risk providers.
     *
     * @param cause describing the reason of failure
     */
    void onRiskInitializedError(Throwable cause);

    /**
     * Called when the risk data has been collected from the risk providers.
     *
     * @param riskData containing the collected risk data from the different risk providers
     */
    void onRiskCollectionSuccess(List<ProviderParameters> riskData);

    /**
     * Called when an error occurred while obtaining risk data from the risk providers.
     *
     * @param cause describing the reason of failure
     */
    void onRiskCollectionError(Throwable cause);
}
