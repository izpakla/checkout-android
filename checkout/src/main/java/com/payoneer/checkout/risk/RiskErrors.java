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

import java.util.ArrayList;
import java.util.List;

import com.payoneer.checkout.model.Parameter;

/**
 * This class is basically an extension of {@link com.payoneer.checkout.risk.RiskProviderResult} with a map
 * of the external and internal errors to send back to OPG as part of the result object
 */
public class RiskErrors {

    public static final String RESULTKEY_EXTERNAL_ERROR = "riskPluginExternalError";
    public static final String RESULTKEY_INTERNAL_ERROR = "riskPluginInternalError";
    private final List<Parameter> riskErrorParameters = new ArrayList<>();

    public void addErrorParameter(Parameter parameter) {
        riskErrorParameters.add(parameter);
    }

    public List<Parameter> getRiskErrorParameters() {
        return riskErrorParameters;
    }
}
