/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.payment.googlepaybraintree;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import com.braintreepayments.api.GooglePayRequest;
import com.payoneer.checkout.core.PaymentException;
import com.payoneer.checkout.model.OperationResult;
import com.payoneer.checkout.model.Parameter;
import com.payoneer.checkout.model.ProviderParameters;

@RunWith(RobolectricTestRunner.class)
public class GooglePayRequestBuilderTest {

    @Test
    public void of() throws PaymentException {
        List<Parameter> params = new ArrayList<>();
        params.add(createParameter("amountInMajorUnits", "1.00"));
        params.add(createParameter("currencyCode", "EUR"));
        params.add(createParameter("environment", "TEST"));
        OperationResult result = new OperationResult();
        ProviderParameters response = new ProviderParameters();
        response.setProviderCode("GOOGLEPAY");
        response.setParameters(params);
        result.setProviderResponse(response);
        assertNotNull(GooglePayRequestBuilder.of(result));
    }

    @Test(expected = PaymentException.class)
    public void buildFailedWithMissingEnvironment() throws PaymentException {
        new GooglePayRequestBuilder()
            .setAmountInMajorUnits("1.00")
            .setCurrencyCode("EUR").build();
    }

    @Test(expected = PaymentException.class)
    public void buildFailedWithMissingAmount() throws PaymentException {
        new GooglePayRequestBuilder()
            .setEnvironment("TEST")
            .setCurrencyCode("EUR").build();
    }

    @Test(expected = PaymentException.class)
    public void buildFailedWithMissingCurrencyCode() throws PaymentException {
        new GooglePayRequestBuilder()
            .setEnvironment("TEST")
            .setAmountInMajorUnits("1.0").build();
    }

    @Test
    public void buildSuccess() throws PaymentException {
        GooglePayRequest request = new GooglePayRequestBuilder()
            .setEnvironment("TEST")
            .setCurrencyCode("EUR")
            .setAmountInMajorUnits("1.0").build();
        assertNotNull(request);
    }

    private Parameter createParameter(final String name, final String value) {
        Parameter parameter = new Parameter();
        parameter.setName(name);
        parameter.setValue(value);
        return parameter;
    }
}