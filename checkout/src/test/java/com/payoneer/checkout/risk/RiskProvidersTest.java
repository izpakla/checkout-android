/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.risk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import com.payoneer.checkout.model.ProviderParameters;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;

@RunWith(RobolectricTestRunner.class)
public class RiskProvidersTest {

    @Test
    public void setInstance() {
        RiskProviders origRiskProviders = new RiskProviders("riskProvidersId");
        RiskProviders.setInstance(origRiskProviders);
        assertEquals(origRiskProviders, RiskProviders.getInstance());
    }

    @Test
    public void containsRiskProvidersId() {
        String riskProvidersId = "riskProvidersId";
        RiskProviders origRiskProviders = new RiskProviders(riskProvidersId);
        RiskProviders.setInstance(origRiskProviders);
        assertTrue(RiskProviders.getInstance().containsRiskProvidersId(riskProvidersId));
    }

    @Test
    public void initializeRiskProvidersWithNullList() {
        Context context = ApplicationProvider.getApplicationContext();
        List<ProviderParameters> providerParameters = null;
        RiskProviders riskProviders = new RiskProviders("riskProvidersId");
        riskProviders.initializeRiskProviders(providerParameters, context);
        assertEquals(0, riskProviders.getRiskProviderRequests(context).size());
    }

    @Test
    public void initializeRiskProvidersWithEmptyList() {
        Context context = ApplicationProvider.getApplicationContext();
        RiskProviders riskProviders = new RiskProviders("riskProvidersId");
        riskProviders.initializeRiskProviders(new ArrayList<>(), context);
        assertEquals(0, riskProviders.getRiskProviderRequests(context).size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void initializeRiskProvidersWithNullContext() {
        RiskProviders riskProviders = new RiskProviders("riskProvidersId");
        riskProviders.initializeRiskProviders(new ArrayList<>(), null);
    }

    @Test
    public void getRiskProviderRequests() {
        List<ProviderParameters> providers = new ArrayList<>();
        providers.add(createProviderParameters("CODE0", "TYPE0"));
        providers.add(createProviderParameters("CODE1", "TYPE1"));

        Context context = ApplicationProvider.getApplicationContext();
        RiskProviders riskProviders = new RiskProviders("riskProvidersId");
        riskProviders.initializeRiskProviders(providers, context);

        List<ProviderParameters> requests = riskProviders.getRiskProviderRequests(context);
        assertEquals(2, requests.size());
        assertTrue(containsProviderParameters(requests, "CODE0", "TYPE0"));
        assertTrue(containsProviderParameters(requests, "CODE1", "TYPE1"));
    }

    private ProviderParameters createProviderParameters(final String code, final String type) {
        ProviderParameters provider = new ProviderParameters();
        provider.setProviderCode(code);
        provider.setProviderType(type);
        return provider;
    }

    private boolean containsProviderParameters(final List<ProviderParameters> parameters, final String code, final String type) {
        for (ProviderParameters param : parameters) {
            if (param.getProviderCode().equals(code) && param.getProviderType().equals(type)) {
                return true;
            }
        }
        return false;
    }
}