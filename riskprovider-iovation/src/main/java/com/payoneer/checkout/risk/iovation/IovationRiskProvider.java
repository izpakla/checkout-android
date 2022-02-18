/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.risk.iovation;

import com.payoneer.checkout.risk.RiskException;
import com.payoneer.checkout.risk.RiskProvider;
import com.payoneer.checkout.risk.RiskProviderInfo;
import com.payoneer.checkout.risk.RiskProviderResult;

import android.content.Context;

import com.iovation.mobile.android.FraudForceConfiguration;
import com.iovation.mobile.android.FraudForceManager;

/**
 * Iovation Risk provider
 */
public class IovationRiskProvider implements RiskProvider {

    private FraudForceManager fraudForceManager;

    /**
     * Get the singleton instance of this Iovation risk provider
     *
     * @return the instance of this risk provider
     */
    public static IovationRiskProvider getInstance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public void initialize(final Context context, final RiskProviderInfo info) throws RiskException {
        if (fraudForceManager != null) {
            return;
        }
        FraudForceConfiguration configuration = new FraudForceConfiguration.Builder()
            .enableNetworkCalls(false)
            .build();

        fraudForceManager = FraudForceManager.getInstance();
        fraudForceManager.initialize(configuration, context);
    }

    @Override
    public RiskProviderResult getRiskProviderResult(final Context context) throws RiskException {
        if (fraudForceManager == null) {
            throw new RiskException("FraudForceManager not initiallized, initialize first");
        }
        return fraudForceManager.getBlackbox(context);
    }

    private static class InstanceHolder {
        static final IovationRiskProvider INSTANCE = new IovationRiskProvider();
    }
}
