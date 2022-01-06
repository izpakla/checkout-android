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
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.payoneer.checkout.core.PaymentException;
import com.payoneer.checkout.core.PaymentInputCategory;
import com.payoneer.checkout.core.PaymentInputType;
import com.payoneer.checkout.form.Operation;
import com.payoneer.checkout.model.AccountInputData;
import com.payoneer.checkout.model.ApplicableNetwork;
import com.payoneer.checkout.model.ListResult;
import com.payoneer.checkout.model.OperationResult;
import com.payoneer.checkout.network.ListConnection;
import com.payoneer.checkout.network.PaymentConnection;
import com.payoneer.checkout.util.PaymentUtils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;
import androidx.test.platform.app.InstrumentationRegistry;

/**
 * Class for creating a new ListUrl
 */
public final class ListService {

    private final String listUrl;
    private final String merchantCode;
    private final String merchantPaymentToken;
    private final ListConnection conn;
    private final PaymentConnection paymentConnection;

    private ListService(final Context context, final String listUrl, final String merchantCode, final String merchantPaymentToken) {
        this.listUrl = listUrl;
        this.merchantCode = merchantCode;
        this.merchantPaymentToken = merchantPaymentToken;
        this.conn = new ListConnection(context);
        this.paymentConnection = new PaymentConnection(context);
    }

    /**
     * Create a new instance of the ListService
     *
     * @param listUrl base url to create a list
     * @param merchantCode code of the test mechant
     * @param merchantPaymentToken secret payment token
     * @return new instance of the ListService
     */
    public static ListService createInstance(String listUrl, String merchantCode, String merchantPaymentToken) {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        return new ListService(context, listUrl, merchantCode, merchantPaymentToken);
    }

    /**
     * Helper method to create list with the provided settings
     *
     * @param settings used to create the list
     * @return the self url of the newly created list
     */
    public String newListSelfUrl(ListSettings settings) throws ListServiceException {
        ListResult listResult = createListResult(settings);
        URL url = PaymentUtils.getSelfURL(listResult);
        if (url == null) {
            throw new ListServiceException("ListResult does not contain a self URL");
        }
        return url.toString();
    }

    /**
     * Helper method to create list with the provided settings
     *
     * @param listUrl url pointing to the Payment API Backend
     * @param merchantCode containing the code of the merchant
     * @param merchantPaymentToken unique token used to make payments
     * @param settings used to create the list
     * @return the self url of the newly created list
     */
    public static String createListWithSettings(String listUrl, String merchantCode, String merchantPaymentToken, ListSettings settings)
        throws ListServiceException {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        ListService service = new ListService(context, listUrl, merchantCode, merchantPaymentToken);
        return service.createListUrl(settings);
    }

    private String createListUrl(ListSettings settings) throws ListServiceException {
        try {
            String listBody = createListRequestBody(settings);
            String authHeader = createAuthHeader();
            ListResult result = conn.createPaymentSession(listUrl, authHeader, listBody);
            Map<String, URL> links = result.getLinks();
            URL selfUrl = links != null ? links.get("self") : null;

            if (selfUrl == null) {
                throw new ListServiceException("Error creating payment session, missing self url");
            }
            return selfUrl.toString();
        } catch (PaymentException | JSONException | IOException e) {
            throw new ListServiceException("Error creating payment session", e);
        }
    }

    private String createListRequestBody(ListSettings settings) throws JSONException, IOException {
        JSONObject json = loadJSONTemplate(settings.getListResId());
        String language = settings.getLanguage();
        if (language != null) {
            JSONObject style = json.getJSONObject("style");
            style.put("language", language);
        }
        BigDecimal amount = settings.getAmount();
        if (amount != null) {
            JSONObject payment = json.getJSONObject("payment");
            payment.put("amount", amount);
        }
        String appId = settings.getAppId();
        if (appId != null) {
            JSONObject callback = json.getJSONObject("callback");
            callback.put("appId", appId);
        }
        String operationType = settings.getOperationType();
        if (operationType != null) {
            json.put("operationType", operationType);
        }
        String checkoutConfigurationName = settings.getCheckoutConfigurationName();
        if (checkoutConfigurationName != null) {
            json.put("checkoutConfigurationName", checkoutConfigurationName);
        }
        String division = settings.getDivision();
        if (division != null) {
            json.put("division", division);
        }
        return json.toString();
    }

    private JSONObject loadJSONTemplate(int jsonResId) throws JSONException, IOException {
        Context context = InstrumentationRegistry.getInstrumentation().getContext();
        String fileContent = PaymentUtils.readRawResource(context.getResources(), jsonResId);
        return new JSONObject(fileContent);
    }

    /**
     * Register a new account. First a new list will be created and then the ApplicableNetwork
     * with the networkCode will be registered using the inputData.
     *
     * @param settings contains the settings to create the list
     * @param networkCode code of the ApplicableNetwork that should be registered
     * @param inputData card data to be registered
     * @return registrationId of the user
     * @throws ListServiceException
     */
    public String registerAccount(ListSettings settings, String networkCode, AccountInputData inputData, boolean autoRegistration,
        boolean allowRecurrence) throws ListServiceException {
        try {
            ListResult listResult = createListResult(settings);
            ApplicableNetwork network = PaymentUtils.getApplicableNetwork(listResult, networkCode);

            if (network == null) {
                throw new ListServiceException("Missing ApplicableNetwork in ListResult: " + networkCode);
            }
            Operation operation = Operation.fromApplicableNetwork(network);
            operation.setAccountInputData(inputData);

            operation.putBooleanValue(PaymentInputCategory.REGISTRATION, PaymentInputType.AUTO_REGISTRATION, autoRegistration);
            operation.putBooleanValue(PaymentInputCategory.REGISTRATION, PaymentInputType.ALLOW_RECURRENCE, allowRecurrence);

            OperationResult result = paymentConnection.postOperation(operation);
            String registrationId = PaymentUtils.getCustomerRegistrationId(result);

            if (TextUtils.isEmpty(registrationId)) {
                throw new ListServiceException("Could not find registrationId in OperationResult");
            }
            return registrationId;
        } catch (PaymentException e) {
            throw new ListServiceException("Error creating payment session", e);
        }
    }

    /**
     * Create a new list result using the list settings
     *
     * @param settings to be used when creating the new list
     * @return newly created list result
     * @throws ListServiceException
     */
    public ListResult createListResult(ListSettings settings) throws ListServiceException {
        try {
            String listRequestBody = ListRequestBuilder.createFromListSettings(settings);
            String authHeader = createAuthHeader();
            return conn.createPaymentSession(listUrl, authHeader, listRequestBody);
        } catch (PaymentException e) {
            throw new ListServiceException("Error creating payment session", e);
        }
    }

    private String createAuthHeader() {
        String header = merchantCode + "/" + merchantPaymentToken;
        byte[] data = header.getBytes(StandardCharsets.UTF_8);
        return Base64.encodeToString(data, Base64.DEFAULT);
    }
}
