/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.risk;

import java.util.ArrayList;
import java.util.List;

import com.payoneer.checkout.model.Parameter;
import com.payoneer.checkout.model.ProviderParameters;

import android.content.Context;

/**
 * The RiskProviders class contains the list of RiskProviderControllers each handling a third-party risk provider service.
 */
public final class RiskProviders {
    private static RiskProviders instance;
    private final List<RiskProviderController> controllers;
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
     * Check if the riskProvidersId stored in this RiskProviders matches with the provided providersId.
     * The listUrl or longID of a list could be used as the unique ID to which all RiskProviderControllers in this RiskProviders belong to.
     *
     * @param providersId to be matched with the riskProvidersId stored in this RiskProviders
     * @return true when contains, false otherwise
     */
    public boolean containsRiskProvidersId(final String providersId) {
        return this.riskProvidersId.equals(providersId);
    }

    /**
     * Initialize all RiskProviderControllers described in the the list of risk ProviderParameters.
     *
     * @param riskProviders list of risk providers that should be initialized
     * @param context contains information about the application environment
     */
    public void initializeRiskProviders(final List<ProviderParameters> riskProviders, final Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        if (riskProviders == null || riskProviders.size() == 0) {
            return;
        }
        Context applicationContext = context.getApplicationContext();
        for (ProviderParameters provider : riskProviders) {
            if (containsRiskController(provider.getProviderCode(), provider.getProviderType())) {
                continue;
            }
            RiskProviderController controller = createRiskController(provider, applicationContext);
            controller.initialize(context);
            controllers.add(controller);
        }
    }

    /**
     * Get all risk request ProviderParameters obtained from the individual RiskProviderControllers.
     *
     * @param context contains information about the application environment
     * @return list of all risk provider requests
     */
    public List<ProviderParameters> getRiskProviderRequests(final Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
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

        RiskProviderError errors = controller.getRiskErrors();
        RiskProviderResult result = controller.getRiskProviderResult(context);
        result.copyInto(parameters, errors.getRiskProviderErrorParameters());
        return request;
    }
}