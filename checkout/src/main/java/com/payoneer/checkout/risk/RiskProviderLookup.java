/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.risk;

import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.CopyOnWriteArrayList;

import android.text.TextUtils;

/**
 * Class for looking up a RiskProvider given the third-party risk provider service code and type.
 */
public class RiskProviderLookup {

    private static final List<RiskProviderFactory> factories = new CopyOnWriteArrayList<>();

    /**
     * Helper method to get a new RiskProvider for the third-party risk provider service with code and type
     *
     * @param riskProviderCode to be used to lookup a RiskProvider
     * @param riskProviderType to be used to lookup a RiskProvider
     * @return the RiskProvider or null if none found
     */
    public static RiskProvider getRiskProvider(String riskProviderCode, String riskProviderType) {
        RiskProviderFactory factory = getRiskProviderFactory(riskProviderCode, riskProviderType);
        return factory != null ? factory.createRiskProvider() : null;
    }

    private static RiskProviderFactory getRiskProviderFactory(String riskProviderCode, String riskProviderType) {
        if (TextUtils.isEmpty(riskProviderCode)) {
            throw new IllegalArgumentException("riskProviderCode cannot be null or empty");
        }
        if (factories.size() == 0) {
            initRiskProviderFactories();
        }
        for (RiskProviderFactory factory : factories) {
            if (factory.supports(riskProviderCode, riskProviderType)) {
                return factory;
            }
        }
        return null;
    }

    private static void initRiskProviderFactories() {
        synchronized (factories) {
            if (factories.size() > 0) {
                return;
            }
            ServiceLoader<RiskProviderFactory> loader = ServiceLoader.load(RiskProviderFactory.class);
            for (RiskProviderFactory factory : loader) {
                factories.add(factory);
            }
        }
    }
}
