/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.risk;

import static com.payoneer.checkout.risk.RiskErrors.RESULTKEY_EXTERNAL_ERROR;
import static com.payoneer.checkout.risk.RiskErrors.RESULTKEY_INTERNAL_ERROR;

import java.util.Map;
import java.util.Objects;

import android.content.Context;
import android.util.Log;

/**
 * RiskProviderController initializes and controls a RiskProvider. It also makes sure to return
 * an empty RiskProviderResult when an error occurred while obtaining the RiskProviderResult from the RiskProvider.
 */
public final class RiskProviderController {

    private final RiskProviderInfo info;
    private RiskProvider riskProvider;
    private final RiskErrors riskErrors = new RiskErrors();

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
     * Match the code and type of this RiskProviderController with the provided riskProviderCode and riskProviderType.
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
     * Initialize the RiskProvider given the Context
     *
     * @param context contains information about the application environment
     */
    public void initialize(final Context context) {
        String code = info.getRiskProviderCode();
        String type = info.getRiskProviderType();
        riskProvider = RiskProviderLookup.getRiskProvider(code, type);

        if (riskProvider == null) {
            String message = "RiskProviderController(" + code + ", " + type + ") could not find RiskProvider";
            riskErrors.put(RESULTKEY_INTERNAL_ERROR, trimMessage(message));
            Log.w("checkout-sdk", message);
            return;
        }
        try {
            Context applicationContext = context.getApplicationContext();
            riskProvider.initialize(info, applicationContext);
        } catch (RiskException e) {
            String message = "RiskProviderController(" + code + ", " + type + ") failed to initialize RiskProvider " + e.getMessage();
            riskErrors.put(RESULTKEY_EXTERNAL_ERROR, trimMessage(message));
            Log.w("checkout-sdk", message, e);
        }
    }

    /**
     * Get the RiskProviderResult from the RiskProvider.
     * If an error occurred while obtaining the RiskProviderResult from the RiskProvider, then return an empty RiskProviderResult.
     *
     * @param context contains information about the application environment
     * @return RiskProviderResult obtained from the RiskProvider
     */
    public RiskProviderResult getRiskProviderResult(final Context context) {
        RiskProviderResult result = null;
        if (riskProvider != null) {
            String code = info.getRiskProviderCode();
            String type = info.getRiskProviderType();

            try {
                Context applicationContext = context.getApplicationContext();
                result = riskProvider.getRiskProviderResult(applicationContext);
                for (Map.Entry<String, String> entry : riskErrors.getRiskErrors().entrySet()) {
                    result.put(entry.getKey(), entry.getValue());
                }
            } catch (RiskException e) {
                String message = "RiskProviderController(" + code + ", " + type + ") could not obtain result " + e.getMessage();
                riskErrors.put(RESULTKEY_EXTERNAL_ERROR, trimMessage(message));
                Log.w("checkout-sdk", message, e);
            }
        }
        return (result != null) ? result : new RiskProviderResult();
    }

    private String trimMessage(String message) {
        if (message.length() > 2000) {
            return message.substring(0, 2000);
        } else {
            return message;
        }
    }
}
