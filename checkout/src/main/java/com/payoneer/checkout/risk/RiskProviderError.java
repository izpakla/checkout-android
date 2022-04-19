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
 * This class is basically an extension of {@link com.payoneer.checkout.risk.RiskProviderResult} with a list
 * of the external and internal errors to send back to OPG as part of the result object
 */
public class RiskProviderError {

    private static final String RESULTKEY_EXTERNAL_ERROR = "riskPluginExternalError";
    private static final String RESULTKEY_INTERNAL_ERROR = "riskPluginInternalError";
    private final List<Parameter> riskProviderErrorParameters = new ArrayList<>();

    public void addInternalErrorParameter(String value) {
        addErrorParameter(RESULTKEY_INTERNAL_ERROR, value);
    }

    public void addExternalErrorParameter(String value) {
        addErrorParameter(RESULTKEY_EXTERNAL_ERROR, value);
    }

    private void addErrorParameter(final String key, final String value) {
        Parameter copyParam = new Parameter();
        copyParam.setName(key);
        copyParam.setValue(trimValue(value));
        riskProviderErrorParameters.add(copyParam);
    }

    public List<Parameter> getRiskProviderErrorParameters() {
        return riskProviderErrorParameters;
    }

    private String trimValue(String message) {
        if (message.length() > 2000) {
            return message.substring(0, 2000);
        } else {
            return message;
        }
    }
}
