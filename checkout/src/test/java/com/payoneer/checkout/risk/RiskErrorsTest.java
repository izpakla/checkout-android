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

    @Test
    public void putShouldReturnCorrectValues() {
        RiskErrors errors = new RiskErrors();
        Parameter parameter = new Parameter();
        parameter.setName("NAME");
        parameter.setValue("VALUE");
        errors.addErrorParameter(parameter);
        List<Parameter> parameters = errors.getRiskErrorParameters();
        assertEquals("NAME", parameters.get(0).getName());
        assertEquals(1, parameters.size());
    }

    @Test
    public void putWithLongMessageShouldTrim() {
        RiskErrors errors = new RiskErrors();
        Parameter parameter = new Parameter();
        parameter.setName("NAME");
        parameter.setValue(generateString(89709890));
        errors.addErrorParameter(parameter);
        List<Parameter> parameters = errors.getRiskErrorParameters();
        assertEquals("NAME", parameters.get(0).getName());
        assertEquals(generateString(2000), parameters.get(0).getValue());
        assertEquals(1, parameters.size());
    }

    private String generateString(int length) {
        return StringUtils.repeat('a', length);
    }
}