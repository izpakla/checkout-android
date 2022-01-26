/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.sharedtest.service;

import java.io.IOException;
import java.math.BigDecimal;

import org.json.JSONException;
import org.json.JSONObject;

import com.payoneer.checkout.util.PaymentUtils;

import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;

/**
 * Class for creating the list request body
 */
public final class ListRequestBuilder {

    private final ListSettings settings;

    public ListRequestBuilder(ListSettings settings) {
        this.settings = settings;
    }

    public static String createFromListSettings(ListSettings settings) throws ListServiceException {
        return new ListRequestBuilder(settings).build();
    }

    public String build() throws ListServiceException {
        try {
            JSONObject json = loadJSONTemplate(settings.getListResId());
            String language = settings.getLanguage();
            putStringIntoChild(json, "style", "language", language);

            BigDecimal amount = settings.getAmount();
            putAmount(json, amount);

            String appId = settings.getAppId();
            putStringIntoChild(json, "callback", "appId", appId);

            String operationType = settings.getOperationType();
            putString(json, "operationType", operationType);

            String checkoutConfigurationName = settings.getCheckoutConfigurationName();
            putString(json, "checkoutConfigurationName", checkoutConfigurationName);

            String division = settings.getDivision();
            putString(json, "division", division);

            String registrationId = settings.getRegistrationId();
            putRegistrationId(json, registrationId);

            return json.toString();
        } catch (JSONException | IOException e) {
            throw new ListServiceException(e);
        }
    }

    private void putString(JSONObject parent, String key, String value) throws JSONException {
        if (value != null) {
            parent.put(key, value);
        }
    }

    private void putStringIntoChild(JSONObject parent, String childKey, String key, String value) throws JSONException {
        JSONObject child = parent.getJSONObject(childKey);
        putString(child, key, value);
    }

    private void putAmount(JSONObject parent, BigDecimal amount) throws JSONException {
        if (amount != null) {
            JSONObject payment = parent.getJSONObject("payment");
            payment.put("amount", amount);
        }
    }

    private void putRegistrationId(JSONObject parent, String registrationId) throws JSONException {
        if (registrationId != null) {
            JSONObject customer = parent.getJSONObject("customer");
            JSONObject registration = new JSONObject();
            registration.put("id", registrationId);
            customer.put("registration", registration);
        }
    }

    private JSONObject loadJSONTemplate(int jsonResId) throws JSONException, IOException {
        Context context = InstrumentationRegistry.getInstrumentation().getContext();
        String fileContent = PaymentUtils.readRawResource(context.getResources(), jsonResId);
        return new JSONObject(fileContent);
    }
}
