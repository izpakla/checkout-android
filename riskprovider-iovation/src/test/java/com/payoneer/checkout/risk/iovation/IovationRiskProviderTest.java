/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.risk.iovation;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import com.payoneer.checkout.risk.RiskException;
import com.payoneer.checkout.risk.RiskProviderInfo;
import com.payoneer.checkout.risk.RiskProviderResult;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;

@RunWith(RobolectricTestRunner.class)
public class IovationRiskProviderTest {

    @Test
    public void initialize() throws RiskException {
        Context context = ApplicationProvider.getApplicationContext();
        IovationRiskProvider provider = IovationRiskProvider.getInstance();
        provider.initialize(createRiskProviderInfo(), context);
    }

    @Test
    public void multipleInitializations() throws RiskException {
        Context context = ApplicationProvider.getApplicationContext();
        IovationRiskProvider provider = IovationRiskProvider.getInstance();
        provider.initialize(createRiskProviderInfo(), context);
        provider.initialize(createRiskProviderInfo(), context);
        provider.initialize(createRiskProviderInfo(), context);
    }

    @Test
    public void getRiskProviderResult() throws RiskException {
        Context context = ApplicationProvider.getApplicationContext();
        IovationRiskProvider provider = IovationRiskProvider.getInstance();
        provider.initialize(createRiskProviderInfo(), context);

        RiskProviderResult result = provider.getRiskProviderResult(context);
        String blackBox = result.getRiskData().get(IovationRiskProvider.RESULTKEY_BLACKBOX);
        assertNotNull(blackBox);
        assertTrue(blackBox.length() > 0);
    }

    private RiskProviderInfo createRiskProviderInfo() {
        Map<String, String> params = new HashMap<>();
        return new RiskProviderInfo(IovationRiskProviderFactory.IOVATION_CODE, IovationRiskProviderFactory.IOVATION_TYPE, params);
    }
}