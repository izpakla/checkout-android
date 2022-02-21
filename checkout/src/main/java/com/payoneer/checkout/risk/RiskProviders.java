/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.risk;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.payoneer.checkout.model.Parameter;
import com.payoneer.checkout.model.ProviderParameters;

import android.content.Context;

/**
 * The RiskProviders class contains the list of risk providers that are initialized
 */
public final class RiskProviders {
    private final List<RiskProviderController> controllers;
    private static RiskProviders instance;
    private final String riskProvidersId;

    public RiskProviders(final String riskProvidersId) {
        this.riskProvidersId = riskProvidersId;
        controllers = new ArrayList<>();
    }

    /**
     * Get the currently set RiskProviders instance
     *
     * @return the current instance or null if not previously set
     */
    public static RiskProviders getInstance() {
        return instance;
    }

    /**
     * Set the current RiskProviders instance
     *
     * @param newInstance to be set as the current instance
     */
    public static void setInstance(RiskProviders newInstance) {
        instance = newInstance;
    }

    /**
     * Check if the unique ID of this RiskProviders matches with the provided ID.
     * For example, the listUrl or longID of a list may be used as the unique ID to which all loaded risk providers are attached to.
     *
     * @param providersId to be matched with the ID of this RiskProviders
     * @return true when contains, false otherwise
     */
    public boolean containsRiskProvidersId(final String providersId) {
        return this.riskProvidersId.equals(providersId);
    }

    /**
     * Initialize all risk providers provided in the list of ProviderParameters.
     *
     * @param providers list of risk providers that should be initialized
     * @param context used to intialize each individual risk provider
     */
    public void initializeRiskProviders(final List<ProviderParameters> providers, final Context context) {
        Objects.requireNonNull(context);
        if (providers == null || providers.size() == 0) {
            return;
        }
        Context applicationContext = context.getApplicationContext();
        for (ProviderParameters provider : providers) {
            if (containsRiskController(provider.getProviderCode(), provider.getProviderType())) {
                continue;
            }
            RiskProviderController controller = createRiskController(provider, applicationContext);
            controller.initialize(context);
            controllers.add(controller);
        }
    }

    /**
     * Get all risk provider request risk data from all loaded risk providers
     *
     * @param context may be used to obtain the risk request data
     * @return list of all risk request data
     */
    public List<ProviderParameters> getRiskProviderRequests(final Context context) {
        Objects.requireNonNull(context);

        List<ProviderParameters> requests = new ArrayList<>();
        Context applicationContext = context.getApplicationContext();
        for (RiskProviderController controller : controllers) {
            requests.add(getRiskProviderRequest(controller, applicationContext));
        }
        return requests;
    }

    private RiskProviderController createRiskController(final ProviderParameters parameters, final Context context) {
        RiskProviderInfo info = RiskProviderInfo.fromProviderParameters(parameters);
        RiskProviderController controller = new RiskProviderController(info);
        controller.initialize(context);
        return controller;
    }

    private boolean containsRiskController(final String code, final String type) {
        for (RiskProviderController controller : controllers) {
            if (controller.matches(code, type)) {
                return true;
            }
        }
        return false;
    }

    private ProviderParameters getRiskProviderRequest(final RiskProviderController controller, final Context context) {
        ProviderParameters request = new ProviderParameters();
        request.setProviderCode(controller.getRiskProviderCode());
        request.setProviderType(controller.getRiskProviderType());

        List<Parameter> parameters = new ArrayList<>();
        request.setParameters(parameters);

        RiskProviderResult result = controller.getRiskProviderResult(context);
        result.copyInto(parameters);
        return request;
    }
}