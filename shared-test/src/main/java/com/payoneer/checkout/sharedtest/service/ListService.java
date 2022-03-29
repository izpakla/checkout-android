/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.sharedtest.service;

import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.payoneer.checkout.core.PaymentException;
import com.payoneer.checkout.core.PaymentInputCategory;
import com.payoneer.checkout.core.PaymentInputType;
import com.payoneer.checkout.model.AccountInputData;
import com.payoneer.checkout.model.ApplicableNetwork;
import com.payoneer.checkout.model.ListResult;
import com.payoneer.checkout.model.OperationResult;
import com.payoneer.checkout.network.ListConnection;
import com.payoneer.checkout.network.Operation;
import com.payoneer.checkout.network.PaymentConnection;
import com.payoneer.checkout.payment.PaymentRequest;
import com.payoneer.checkout.util.PaymentUtils;

import android.text.TextUtils;
import android.util.Base64;

/**
 * Class for creating a new ListUrl
 */
public final class ListService {

    private final String listUrl;
    private final String merchantCode;
    private final String merchantPaymentToken;
    private final ListConnection listConnection;
    private final PaymentConnection paymentConnection;

    private ListService(final String listUrl, final String merchantCode, final String merchantPaymentToken) {
        this.listUrl = listUrl;
        this.merchantCode = merchantCode;
        this.merchantPaymentToken = merchantPaymentToken;
        this.listConnection = new ListConnection();
        this.paymentConnection = new PaymentConnection();
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
        return new ListService(listUrl, merchantCode, merchantPaymentToken);
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
     * Register a new account. First a new list will be created and then the ApplicableNetwork
     * with the networkCode will be registered using the inputData.
     *
     * @param settings contains the settings to create the list
     * @param networkCode code of the ApplicableNetwork that should be registered
     * @param inputData card data to be registered
     * @param autoRegistration auto registration flag
     * @param allowRecurrence allow recurrence flag
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
            PaymentRequest paymentRequest = PaymentRequest.fromApplicableNetwork(network);
            paymentRequest.setAccountInputData(inputData);

            paymentRequest.putBooleanValue(PaymentInputCategory.REGISTRATION, PaymentInputType.AUTO_REGISTRATION, autoRegistration);
            paymentRequest.putBooleanValue(PaymentInputCategory.REGISTRATION, PaymentInputType.ALLOW_RECURRENCE, allowRecurrence);
            Operation operation = new Operation(paymentRequest.getOperationLink(), paymentRequest.getOperationData());

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
            return listConnection.createPaymentSession(listUrl, authHeader, listRequestBody);
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