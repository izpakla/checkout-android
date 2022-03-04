/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.risk.iovation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.payoneer.checkout.risk.RiskProvider;

public class IovationRiskProviderFactoryTest {

    @Test
    public void supports() {
        IovationRiskProviderFactory factory = new IovationRiskProviderFactory();
        assertTrue(factory.supports(IovationRiskProviderFactory.IOVATION_CODE, IovationRiskProviderFactory.IOVATION_TYPE));
        assertFalse(factory.supports(IovationRiskProviderFactory.IOVATION_CODE, null));
        assertFalse(factory.supports(null, IovationRiskProviderFactory.IOVATION_TYPE));
        assertFalse(factory.supports(null, null));
        assertFalse(factory.supports("foo", "foo"));
    }

    @Test
    public void createRiskProvider() {
        IovationRiskProviderFactory factory = new IovationRiskProviderFactory();
        RiskProvider riskProvider = factory.createRiskProvider();
        assertNotNull(riskProvider);
        assertTrue(riskProvider instanceof IovationRiskProvider);
    }
}