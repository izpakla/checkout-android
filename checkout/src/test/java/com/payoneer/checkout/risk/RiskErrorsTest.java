/*
 *
 *   Copyright (c) 2022 Payoneer Germany GmbH
 *   https://www.payoneer.com
 *
 *   This file is open source and available under the MIT license.
 *   See the LICENSE file for more information.
 *
 */

package com.payoneer.checkout.risk;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.payoneer.checkout.model.Parameter;

public class RiskErrorsTest {

    private final String RESULTKEY_INTERNAL_ERROR = "riskPluginInternalError";
    private final String RESULTKEY_EXTERNAL_ERROR = "riskPluginExternalError";

    @Test
    public void putShouldReturnCorrectValuesForInternalErrors() {
        RiskErrors errors = new RiskErrors();
        errors.addInternalErrorParameter("INTERNAL_VALUE");
        List<Parameter> parameters = errors.getRiskErrorParameters();
        Parameter firstParam = parameters.get(0);
        assertEquals(RESULTKEY_INTERNAL_ERROR, firstParam.getName());
        assertEquals("INTERNAL_VALUE", firstParam.getValue());
        assertEquals(1, parameters.size());
    }

    @Test
    public void putShouldReturnCorrectValuesForExternalErrors() {
        RiskErrors errors = new RiskErrors();
        errors.addExternalErrorParameter("EXTERNAL_VALUE");
        List<Parameter> parameters = errors.getRiskErrorParameters();
        Parameter firstParam = parameters.get(0);
        assertEquals(RESULTKEY_EXTERNAL_ERROR, firstParam.getName());
        assertEquals("EXTERNAL_VALUE", firstParam.getValue());
        assertEquals(1, parameters.size());
    }

    @Test
    public void putWithLongMessageForInternalErrorsShouldTrim() {
        RiskErrors errors = new RiskErrors();
        errors.addInternalErrorParameter(generateString(89709890));
        List<Parameter> parameters = errors.getRiskErrorParameters();
        Parameter firstParam = parameters.get(0);
        assertEquals(RESULTKEY_INTERNAL_ERROR, firstParam.getName());
        assertEquals(generateString(2000), firstParam.getValue());
        assertEquals(1, parameters.size());
    }

    @Test
    public void putWithLongMessageForExternalErrorsShouldTrim() {
        RiskErrors errors = new RiskErrors();
        errors.addExternalErrorParameter(generateString(89709890));
        List<Parameter> parameters = errors.getRiskErrorParameters();
        Parameter firstParam = parameters.get(0);
        assertEquals(RESULTKEY_EXTERNAL_ERROR, firstParam.getName());
        assertEquals(generateString(2000), firstParam.getValue());
        assertEquals(1, parameters.size());
    }

    private String generateString(int length) {
        return StringUtils.repeat('a', length);
    }
}