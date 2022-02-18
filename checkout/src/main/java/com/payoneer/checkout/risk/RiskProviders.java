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

import com.payoneer.checkout.model.Parameter;
import com.payoneer.checkout.model.ProviderParameters;

import android.content.Context;

/**
 * The RiskProviders class contains the list of risk providers initialized.
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

    public boolean containsRiskProvidersId(final String providersId) {
        return this.riskProvidersId.equals(providersId);
    }

    public void initializeRiskProviders(final List<ProviderParameters> providers, final Context context) {
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

    public List<ProviderParameters> getRiskProviderRequests(final Context context) {
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