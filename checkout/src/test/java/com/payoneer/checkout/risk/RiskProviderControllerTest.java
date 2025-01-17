/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.risk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import com.payoneer.checkout.model.Parameter;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;

@RunWith(RobolectricTestRunner.class)
public class RiskProviderControllerTest {

    private final String RESULTKEY_INTERNAL_ERROR = "riskPluginInternalError";

    public static boolean contains(Collection<Parameter> parameters, String value, String name) {
        for (Parameter parameter : parameters) {
            if (parameter != null && parameter.getValue().equals(value) && parameter.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    @Test
    public void getRiskProviderInfo() {
        RiskProviderInfo info = createRiskProviderInfo("CODE", "TYPE");
        RiskProviderController controller = new RiskProviderController(info);
        assertEquals(info, controller.getRiskProviderInfo());
    }

    @Test
    public void getRiskProviderCode() {
        RiskProviderInfo info = createRiskProviderInfo("CODE", "TYPE");
        RiskProviderController controller = new RiskProviderController(info);
        assertEquals("CODE", controller.getRiskProviderCode());
    }

    @Test
    public void getRiskProviderType() {
        RiskProviderInfo info = createRiskProviderInfo("CODE", "TYPE");
        RiskProviderController controller = new RiskProviderController(info);
        assertEquals("TYPE", controller.getRiskProviderType());
    }

    @Test
    public void matches() {
        RiskProviderInfo info = createRiskProviderInfo("CODE", "TYPE");
        RiskProviderController controller = new RiskProviderController(info);
        assertTrue(controller.matches("CODE", "TYPE"));
    }

    @Test
    public void getRiskProviderResult() {
        Context context = ApplicationProvider.getApplicationContext();
        RiskProviderInfo info = createRiskProviderInfo("CODE", "TYPE");
        RiskProviderController controller = new RiskProviderController(info);
        controller.initialize(context);

        RiskProviderResult result = controller.getRiskProviderResult(context);
        assertNotNull(result);
    }

    @Test
    public void getRiskProviderResultWithInternalError() {
        RiskProviderInfo info = createRiskProviderInfo("CODE", "TYPE");
        RiskProviderController controller = new RiskProviderController(info);
        controller.initialize(null);

        String errorMessage = "Could not find RiskProvider[CODE, TYPE]";
        RiskProviderResult result = controller.getRiskProviderResult(ApplicationProvider.getApplicationContext());
        Map<String, String> riskData = result.getRiskData();

        assertNotNull(result);
        assertTrue(riskData.containsKey(RESULTKEY_INTERNAL_ERROR));
        assertEquals(errorMessage, riskData.get(RESULTKEY_INTERNAL_ERROR));
    }

    private RiskProviderInfo createRiskProviderInfo(final String code, final String type) {
        Map<String, String> parameters = new HashMap<>();
        return new RiskProviderInfo(code, type, parameters);
    }
}