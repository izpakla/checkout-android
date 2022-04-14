/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.risk;

import static com.payoneer.checkout.risk.RiskProviderControllerTest.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.payoneer.checkout.model.Parameter;

public class RiskProviderResultTest {

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

        List<Parameter> parameters = new ArrayList<>();
        result.copyInto(parameters, new ArrayList<>());

        Parameter param = parameters.get(0);
        assertEquals("NAME", param.getName());
        assertEquals("VALUE", param.getValue());
    }

    @Test
    public void copyIntoWithErrorParameters() {
        RiskProviderResult result = new RiskProviderResult();
        result.put("NAME", "VALUE");
        List<Parameter> errorParams = new ArrayList<>();
        Parameter parameter = new Parameter();
        parameter.setName("NEW_NAME");
        parameter.setValue("NEW_VALUE");
        errorParams.add(parameter);

        List<Parameter> parameters = new ArrayList<>();
        result.copyInto(parameters, errorParams);

        assertEquals(2, parameters.size());
        assertTrue(contains(parameters, parameter.getValue(), parameter.getName()));
        assertTrue(contains(parameters, "VALUE", "NAME"));
    }
}