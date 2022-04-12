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

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;

@RunWith(RobolectricTestRunner.class)
public class RiskProviderControllerTest {

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
    public void getRiskProviderResultWithErrors() {
        RiskProviderInfo info = createRiskProviderInfo("CODE", "TYPE");
        RiskProviderController controller = new RiskProviderController(info);
        controller.initialize(null);

        RiskProviderResult result = controller.getRiskProviderResult(ApplicationProvider.getApplicationContext());
        assertNotNull(result);
        assertTrue(controller.getRiskErrors().getRiskErrorParameters().stream().anyMatch(parameter -> parameter.getValue()
            .equals("RiskProviderController(CODE, TYPE) could not find RiskProvider")));
    }

    private RiskProviderInfo createRiskProviderInfo(final String code, final String type) {
        Map<String, String> parameters = new HashMap<>();
        return new RiskProviderInfo(code, type, parameters);
    }
}