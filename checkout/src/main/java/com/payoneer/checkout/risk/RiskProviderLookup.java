/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.risk;

import java.util.List;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.concurrent.CopyOnWriteArrayList;

import android.content.Context;
import android.util.Log;

/**
 * Class for looking up a RiskProvider given the risk provider code and type.
 */
public class RiskProviderLookup {

    private static final List<RiskProviderFactory> factories = new CopyOnWriteArrayList<>();
    private static final FallbackRiskProviderFactory fallbackFactory = new FallbackRiskProviderFactory();

    /**
     * Helper class to get anew Risk provider for the given providerCode and providerType
     *
     * @param providerCode to be used to lookup a RiskProvider
     * @param providerType to be used to lookup a RiskProvider
     * @return the RiskProvider or null if none found
     */
    public static RiskProvider getRiskProvider(Context context, String providerCode, String providerType) {
        RiskProviderFactory factory = getRiskProviderFactory(providerCode, providerType);
        return factory != null ? factory.createRiskProvider(context) : null;
    }

    private static RiskProviderFactory getRiskProviderFactory(String providerCode, String providerType) {
        Objects.requireNonNull(providerCode);
        Objects.requireNonNull(providerType);

        if (factories.size() == 0) {
            initRiskProviderFactories();
        }
        for (RiskProviderFactory factory : factories) {
            if (factory.supports(providerCode, providerType)) {
                return factory;
            }
        }
        return fallbackFactory;
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
