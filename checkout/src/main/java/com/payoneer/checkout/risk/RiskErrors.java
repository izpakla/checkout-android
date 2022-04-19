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

    private static final String RESULTKEY_EXTERNAL_ERROR = "riskPluginExternalError";
    private static final String RESULTKEY_INTERNAL_ERROR = "riskPluginInternalError";
    private final List<Parameter> riskErrorParameters = new ArrayList<>();

    public void addInternalErrorParameter(String value) {
        Parameter copyParam = new Parameter();
        copyParam.setName(RESULTKEY_INTERNAL_ERROR);
        copyParam.setValue(trimValue(value));
        riskErrorParameters.add(copyParam);
    }

    public void addExternalErrorParameter(String value) {
        Parameter copyParam = new Parameter();
        copyParam.setName(RESULTKEY_EXTERNAL_ERROR);
        copyParam.setValue(trimValue(value));
        riskErrorParameters.add(copyParam);
    }

    public List<Parameter> getRiskErrorParameters() {
        return riskErrorParameters;
    }

    private String trimValue(String message) {
        if (message.length() > 2000) {
            return message.substring(0, 2000);
        } else {
            return message;
        }
    }
}
