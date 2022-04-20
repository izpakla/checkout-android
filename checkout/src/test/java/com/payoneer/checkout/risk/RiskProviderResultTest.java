/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.risk;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.payoneer.checkout.model.Parameter;

public class RiskProviderResultTest {

    private final String RESULTKEY_INTERNAL_ERROR = "riskPluginInternalError";
    private final String RESULTKEY_EXTERNAL_ERROR = "riskPluginExternalError";

    @Test
    public void put() {
        RiskProviderResult result = new RiskProviderResult();
        result.put("NAME", "VALUE");
        Map<String, String> data = result.getRiskData();
        assertEquals("VALUE", data.get("NAME"));
    }

    @Test
    public void copyInto() {
        RiskProviderResult result = new RiskProviderResult();
        result.put("NAME", "VALUE");

        List<Parameter> parameters = result.getProviderResultParameters();
        assertEquals(1, parameters.size());

        Parameter param = parameters.get(0);
        assertEquals("NAME", param.getName());
        assertEquals("VALUE", param.getValue());
    }

    @Test
    public void from() {
        RiskProviderErrors errors = new RiskProviderErrors();
        errors.putExternalError("external error");
        errors.putInternalError("internal error");

        RiskProviderResult result = RiskProviderResult.of(errors);
        Map<String, String> riskData = result.getRiskData();

        assertEquals(2, riskData.size());
        assertEquals("external error", riskData.get(RESULTKEY_EXTERNAL_ERROR));
        assertEquals("internal error", riskData.get(RESULTKEY_INTERNAL_ERROR));
    }
}