/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.risk.iovation;

import com.payoneer.checkout.risk.RiskProvider;
import com.payoneer.checkout.risk.RiskProviderFactory;

public class IovationRiskProviderFactory implements RiskProviderFactory {

    public final static String IOVATION_CODE = "IOVATION";
    public final static String IOVATION_TYPE = "RISK_DATA_PROVIDER";

    public IovationRiskProviderFactory() {
    }

    @Override
    public boolean supports(final String riskProviderCode, final String riskProviderType) {
        return IOVATION_CODE.equals(riskProviderCode) && IOVATION_TYPE.equals(riskProviderType);
    }

    @Override
    public RiskProvider createRiskProvider() {
        return IovationRiskProvider.getInstance();
    }
}
