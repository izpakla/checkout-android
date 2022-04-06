/*
 *
 *   Copyright (c) 2022 Payoneer Germany GmbH
 *   https://www.payoneer.com
 *
 *   This file is open source and available under the MIT license.
 *   See the LICENSE file for more information.
 *
 */

package com.payoneer.checkout.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;

import android.app.Activity;

public class GoogleAdyenUtils {

    private static final int PAYMENTS_ENVIRONMENT = WalletConstants.ENVIRONMENT_TEST;
    private static final String CURRENCY_CODE = "USD";
    private static final String COUNTRY_CODE = "US";
    private static final List<String> SHIPPING_SUPPORTED_COUNTRIES = Arrays.asList("US", "GB");
    private static final BigDecimal CENTS_IN_A_UNIT = new BigDecimal(100d);

    private static JSONObject getTransactionInfo(String price) throws JSONException {
        JSONObject transactionInfo = new JSONObject();
        transactionInfo.put("totalPrice", price);
        transactionInfo.put("totalPriceStatus", "FINAL");
        transactionInfo.put("countryCode", COUNTRY_CODE);
        transactionInfo.put("currencyCode", CURRENCY_CODE);
        transactionInfo.put("checkoutOption", "COMPLETE_IMMEDIATE_PURCHASE");

        return transactionInfo;
    }

    private static JSONObject getMerchantInfo() throws JSONException {
        return new JSONObject().put("merchantName", "PAYONEER TEST");
    }

    public static Optional<JSONObject> getPaymentDataRequest(String gatewayMerchantId) {
        final String price = centsToString(213123);

        try {
            JSONObject paymentDataRequest = GoogleAdyenUtils.getBaseRequest();
            paymentDataRequest.put(
                "allowedPaymentMethods", new JSONArray().put(getCardPaymentMethod(gatewayMerchantId)));
            paymentDataRequest.put("transactionInfo", getTransactionInfo(price));
            paymentDataRequest.put("merchantInfo", getMerchantInfo());

      /* An optional shipping address requirement is a top-level property of the PaymentDataRequest
      JSON object. */
            paymentDataRequest.put("shippingAddressRequired", true);

            JSONObject shippingAddressParameters = new JSONObject();
            JSONArray allowedCountryCodes = new JSONArray(SHIPPING_SUPPORTED_COUNTRIES);

            shippingAddressParameters.put("phoneNumberRequired", false);
            shippingAddressParameters.put("allowedCountryCodes", allowedCountryCodes);

            paymentDataRequest.put("shippingAddressParameters", shippingAddressParameters);
            return Optional.of(paymentDataRequest);

        } catch (JSONException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public static PaymentsClient createPaymentsClient(Activity activity) {
        Wallet.WalletOptions walletOptions =
            new Wallet.WalletOptions.Builder().setEnvironment(PAYMENTS_ENVIRONMENT).build();
        return Wallet.getPaymentsClient(activity, walletOptions);
    }

    public static Optional<JSONObject> getIsReadyToPayRequest() {
        try {
            JSONObject isReadyToPayRequest = getBaseRequest();
            isReadyToPayRequest.put(
                "allowedPaymentMethods", new JSONArray().put(getBaseCardPaymentMethod()));

            return Optional.of(isReadyToPayRequest);

        } catch (JSONException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private static JSONObject getGatewayTokenizationSpecification(String gatewayMerchantId) throws JSONException {
        JSONObject parameters = new JSONObject()
            .put("gateway", "adyen")
            .put("gatewayMerchantId", gatewayMerchantId);

        return new JSONObject()
            .put("type", "PAYMENT_GATEWAY")
            .put("parameters", parameters);
    }

    private static JSONObject getCardPaymentMethod(String gatewayMerchantId) throws JSONException {
        JSONObject cardPaymentMethod = getBaseCardPaymentMethod();
        cardPaymentMethod.put("tokenizationSpecification", getGatewayTokenizationSpecification(gatewayMerchantId));

        return cardPaymentMethod;
    }

    private static JSONObject getBaseCardPaymentMethod() throws JSONException {
        JSONObject cardPaymentMethod = new JSONObject();
        cardPaymentMethod.put("type", "CARD");

        JSONObject parameters = new JSONObject();
        parameters.put("allowedAuthMethods", getAllowedCardAuthMethods());
        parameters.put("allowedCardNetworks", getAllowedCardNetworks());
        // Optionally, you can add billing address/phone number associated with a CARD payment method.
        parameters.put("billingAddressRequired", true);

        JSONObject billingAddressParameters = new JSONObject();
        billingAddressParameters.put("format", "FULL");

        parameters.put("billingAddressParameters", billingAddressParameters);

        cardPaymentMethod.put("parameters", parameters);

        return cardPaymentMethod;
    }

    private static JSONArray getAllowedCardNetworks() {
        return new JSONArray()
            .put("AMEX")
            .put("DISCOVER")
            .put("INTERAC")
            .put("JCB")
            .put("MASTERCARD")
            .put("MIR")
            .put("VISA");
    }

    private static JSONArray getAllowedCardAuthMethods() {
        return new JSONArray()
            .put("PAN_ONLY")
            .put("CRYPTOGRAM_3DS");
    }

    // Create a base request object that contains properties that are present in all other request objects
    private static JSONObject getBaseRequest() throws JSONException {
        return new JSONObject().put("apiVersion", 2).put("apiVersionMinor", 0);
    }

    /**
     * Converts cents to a string format accepted by {@link GoogleAdyenUtils#getPaymentDataRequest}.
     *
     * @param cents value of the price in cents.
     */
    public static String centsToString(long cents) {
        return new BigDecimal(cents)
            .divide(CENTS_IN_A_UNIT, RoundingMode.HALF_EVEN)
            .setScale(2, RoundingMode.HALF_EVEN)
            .toString();
    }
}