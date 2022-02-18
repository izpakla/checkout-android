/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.risk;

import static com.payoneer.checkout.localization.LocalizationTest.createMapLocalizationHolder;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import com.payoneer.checkout.model.Parameter;
import com.payoneer.checkout.model.ProviderParameters;

public class RiskProviderInfoTest {


    @Test(expected = NullPointerException.class)
    public void fromProviderParameters_nullValue() {
        RiskProviderInfo.fromProviderParameters(null);
    }

    @Test
    public void fromProviderParameters() {
        ProviderParameters riskProvider = new ProviderParameters();
        riskProvider.setProviderCode("RISKPROVIDER_CODE");
        riskProvider.setProviderType("RISKPROVIDER_TYPE");
        List<Parameter> parameters = new ArrayList<>();
        riskProvider.setParameters(parameters);

        Parameter param0 = new Parameter();
        param0.setName("PARAM0_NAME");
        param0.setValue("PARAM0_VALUE");
        parameters.add(param0);

        Parameter param1 = new Parameter();
        param1.setName("PARAM1_NAME");
        param1.setValue("PARAM1_VALUE");
        parameters.add(param1);

        RiskProviderInfo info = RiskProviderInfo.fromProviderParameters(riskProvider);
        assertEquals("RISKPROVIDER_CODE", info.getRiskProviderCode());
        assertEquals("RISKPROVIDER_TYPE", info.getRiskProviderType());

        Map<String, String> infoParameters = info.getParameters();
        assertEquals("PARAM0_VALUE", infoParameters.get("PARAM0_NAME"));
        assertEquals("PARAM1_VALUE", infoParameters.get("PARAM1_NAME"));
        assertEquals(parameters.size(), infoParameters.size());
    }
}