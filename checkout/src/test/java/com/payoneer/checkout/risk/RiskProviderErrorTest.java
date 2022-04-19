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
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

public class RiskProviderErrorTest {

    private final String RESULTKEY_INTERNAL_ERROR = "riskPluginInternalError";
    private final String RESULTKEY_EXTERNAL_ERROR = "riskPluginExternalError";

    @Test
    public void putShouldReturnCorrectValuesForInternalErrors() {
        RiskProviderErrors riskProviderErrors = new RiskProviderErrors();
        riskProviderErrors.putInternalError("INTERNAL_VALUE");
        Map<String, String> errors = riskProviderErrors.getErrors();
        assertTrue(errors.containsKey(RESULTKEY_INTERNAL_ERROR));
        assertEquals("INTERNAL_VALUE", errors.get(RESULTKEY_INTERNAL_ERROR));
        assertEquals(1, errors.size());
    }

    @Test
    public void putShouldReturnCorrectValuesForExternalErrors() {
        RiskProviderErrors riskProviderErrors = new RiskProviderErrors();
        riskProviderErrors.putExternalError("EXTERNAL_VALUE");
        Map<String, String> errors = riskProviderErrors.getErrors();
        assertTrue(errors.containsKey(RESULTKEY_EXTERNAL_ERROR));
        assertEquals("EXTERNAL_VALUE", errors.get(RESULTKEY_EXTERNAL_ERROR));
        assertEquals(1, errors.size());
    }

    @Test
    public void putWithLongMessageForInternalErrorsShouldTrim() {
        RiskProviderErrors riskProviderErrors = new RiskProviderErrors();
        riskProviderErrors.putInternalError(generateString(9890));
        Map<String, String> errors = riskProviderErrors.getErrors();
        assertTrue(errors.containsKey(RESULTKEY_INTERNAL_ERROR));
        assertEquals(generateString(2000), errors.get(RESULTKEY_INTERNAL_ERROR));
        assertEquals(1, errors.size());
    }

    @Test
    public void putWithLongMessageForExternalErrorsShouldTrim() {
        RiskProviderErrors riskProviderErrors = new RiskProviderErrors();
        riskProviderErrors.putExternalError(generateString(9890));
        Map<String, String> errors = riskProviderErrors.getErrors();
        assertTrue(errors.containsKey(RESULTKEY_EXTERNAL_ERROR));
        assertEquals(generateString(2000), errors.get(RESULTKEY_EXTERNAL_ERROR));
        assertEquals(1, errors.size());
    }

    private String generateString(int length) {
        return StringUtils.repeat('a', length);
    }
}