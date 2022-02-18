/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.risk;

import java.util.Objects;

import android.content.Context;
import android.util.Log;

/**
 * RiskProviderController handles the loaded RiskProvider, it also makes sure to return
 * default risk data when the RiskProvider could not be initialized.
 */
public final class RiskProviderController {

    private final RiskProviderInfo info;
    private RiskProvider riskProvider;

    public RiskProviderController(final RiskProviderInfo info) {
        this.info = info;
    }

    public RiskProviderInfo getRiskProviderInfo() {
        return info;
    }

    public String getRiskProviderCode() {
        return info.getRiskProviderCode();
    }

    public String getRiskProviderType() {
        return info.getRiskProviderType();
    }

    /**
     * Match this risk provider provider code and type with the provided ones.
     *
     * @param riskProviderCode code of the risk provider to match
     * @param riskProviderType type of the risk provider to match
     * @return true when the code and type matches, false otherwise
     */
    public boolean matches(final String riskProviderCode, final String riskProviderType) {
        return (Objects.equals(getRiskProviderCode(), riskProviderCode)) &&
            (Objects.equals(getRiskProviderType(), riskProviderType));
    }

    /**
     * Initialize the risk provider controlled by this RiskProviderController
     *
     * @param context into which the risk provider will be initialized
     */
    public void initialize(Context context) {
        String code = info.getRiskProviderCode();
        String type = info.getRiskProviderType();
        riskProvider = RiskProviderLookup.getRiskProvider(context, code, type);
        if (riskProvider == null) {
            String message = "RiskProvider(" + code + ", " + type + ") was not found";
            Log.w("checkout", message);
            return;
        }
        try {
            riskProvider.initialize(context.getApplicationContext(), info);
        } catch (RiskException e) {
            String message = "RiskProvider(" + code + ", " + type + ") failed to load";
            Log.w("checkout", message, e);
        }
    }

    /**
     * Get the risk data generated by the loaded risk provider.
     * If an error occurred while obtaining risk result data from the risk provider then return an empty result back
     *
     * @return risk result data obtained from the risk provider
     */
    public RiskProviderResult getRiskProviderResult() {
        RiskProviderResult result = null;
        if (riskProvider != null) {
            try {
                result = riskProvider.getRiskProviderResult();
            } catch (RiskException e) {
                String code = info.getRiskProviderCode();
                String type = info.getRiskProviderType();
                String message = "RiskProvider(" + code + ", " + type + ") result could not be obtained";
                Log.w("checkout", message, e);
            }
        }
        return (result != null) ? result : new RiskProviderResult();
    }
}
