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
}