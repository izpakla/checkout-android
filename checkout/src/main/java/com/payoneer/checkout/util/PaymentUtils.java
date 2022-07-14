/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.util;

import static com.payoneer.checkout.model.PaymentMethod.CREDIT_CARD;
import static com.payoneer.checkout.model.PaymentMethod.DEBIT_CARD;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import com.payoneer.checkout.core.PaymentInputType;
import com.payoneer.checkout.model.ApplicableNetwork;
import com.payoneer.checkout.model.InputElement;
import com.payoneer.checkout.model.ListResult;
import com.payoneer.checkout.model.Networks;
import com.payoneer.checkout.model.OperationData;
import com.payoneer.checkout.model.OperationResult;
import com.payoneer.checkout.model.Parameter;
import com.payoneer.checkout.model.ProviderParameters;
import com.payoneer.checkout.model.Redirect;

import android.content.res.Resources;
import android.view.View;

/**
 * The PaymentUtils class containing helper methods
 */
public final class PaymentUtils {

    /**
     * Check if the Boolean object is true, the Boolean object may be null.
     *
     * @param value the value to check
     * @return true when the val is not null and true
     */
    public static boolean isTrue(Boolean value) {
        return value != null && value;
    }

    /**
     * Return the boolean value given the Boolean Object.
     * If the Object is null then return the default value.
     *
     * @param value the value to check
     * @return defaultValue if value is null, else the boolean value
     */
    public static boolean toBoolean(Boolean value, boolean defaultValue) {
        return (value == null) ? defaultValue : value.booleanValue();
    }

    /**
     * Get the base integer value given the Integer object.
     * If the object is null then return the 0 value.
     *
     * @param value to convert to an integer
     * @return the value as an integer or 0 if the value is null
     */
    public static int toInt(Integer value) {
        return value == null ? 0 : value;
    }

    /**
     * Strips whitespace from the start and end of a String returning an empty String if null input.
     *
     * @param value the String to be trimmed, may be null
     * @return the trimmed String, or an empty String if null input
     */
    public static String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    /**
     * Returns a formatted string using the default locale, format string, and arguments.
     *
     * @param format A format string
     * @param args Arguments referenced by the format specifiers in the format string
     */
    public static String format(String format, Object... args) {
        return String.format(Locale.getDefault(), format, args);
    }

    /**
     * Check if the paymentMethod is a card payment method.
     *
     * @param paymentMethod to be checked if it is a card payment
     * @return true when card payment, false otherwise
     */
    public static boolean isCardPaymentMethod(String paymentMethod) {
        return DEBIT_CARD.equals(paymentMethod) || CREDIT_CARD.equals(paymentMethod);
    }

    /**
     * Compare String values of two Objects by obtaining the String values using the toString method.
     *
     * @param obj1 the first object
     * @param obj2 the second object
     * @return true when the String values of both Objects are equal and not null, false otherwise.
     */
    public static boolean equalsAsString(Object obj1, Object obj2) {
        String str1 = obj1 != null ? obj1.toString() : null;
        String str2 = obj2 != null ? obj2.toString() : null;
        return str1 != null && (str1.equals(str2));
    }

    /**
     * Does the list of InputElements contain both the expiry month and year fields.
     *
     * @param elements list of input elements to check
     * @return true when there are both an expiry month and year
     */
    public static boolean containsExpiryDate(List<InputElement> elements) {
        boolean hasExpiryMonth = false;
        boolean hasExpiryYear = false;

        for (InputElement element : elements) {
            switch (element.getName()) {
                case PaymentInputType.EXPIRY_MONTH:
                    hasExpiryMonth = true;
                    break;
                case PaymentInputType.EXPIRY_YEAR:
                    hasExpiryYear = true;
            }
        }
        return hasExpiryYear && hasExpiryMonth;
    }

    /**
     * Create a full expiry year from the last part of the expiry year.
     * This will use dynamic windowing of -30 years and +70 year.
     *
     * @param inputYear the year which the user entered, e.g. 1 or 99
     * @return complete expiry year value
     */
    public static int createExpiryYear(int inputYear) {
        if (inputYear < 0 || inputYear >= 100) {
            throw new IllegalArgumentException("Input year must be >= 0 and < 100: " + inputYear);
        }
        final int curYear = Calendar.getInstance().get(Calendar.YEAR);
        final int startYear = curYear - 30;
        final int endYear = curYear + 70;

        int year = inputYear > (startYear % 100) ? startYear : endYear;
        return (year - (year % 100)) + inputYear;
    }

    /**
     * Read the contents of the raw resource
     *
     * @param res The system Resources
     * @param resId The resource id
     * @return The String or an empty string if something went wrong
     */
    public static String readRawResource(Resources res, int resId) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;

        try (InputStream is = res.openRawResource(resId);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr)) {

            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (Resources.NotFoundException e) {
            throw new IOException("Resource not found: " + resId);
        }
        return sb.toString();
    }

    /**
     * Get the parameter value given the key of the parameter
     *
     * @param key name of the parameter
     * @param parameters list of parameters to search through
     * @return the value of the parameter or null if the parameter does not exist
     */
    public static String getParameterValue(String key, List<Parameter> parameters) {
        if (parameters == null) {
            return null;
        }
        for (Parameter parameter : parameters) {
            if (key.equals(parameter.getName())) {
                return parameter.getValue();
            }
        }
        return null;
    }

    /**
     * Get the self URL from the listResult
     *
     * @param listResult containing the self url
     * @return the self url from the listResult or null if not found
     */
    public static URL getSelfURL(ListResult listResult) {
        Map<String, URL> links = listResult.getLinks();
        return links != null ? links.get("self") : null;
    }

    /**
     * Get the URL from the map of URLs with the provided key
     *
     * @param key of the url
     * @param urls from where to find the URL
     * @return the url with the key or null if not found
     */
    public static URL getURL(final String key, final Map<String, URL> urls) {
        if (urls == null || !urls.containsKey(key)) {
            return null;
        }
        return urls.get(key);
    }

    /**
     * Get the applicableNetwork from the listResult
     *
     * @param listResult contains the applicableNetwork
     * @param networkCode network code of the applicable network
     * @return the applicableNetwork or null if not found
     */
    public static ApplicableNetwork getApplicableNetwork(ListResult listResult, String networkCode) {
        Networks networks = listResult.getNetworks();
        if (networks == null) {
            return null;
        }
        List<ApplicableNetwork> list = networks.getApplicable();
        if (list == null || list.isEmpty()) {
            return null;
        }
        for (ApplicableNetwork network : list) {
            if (network.getCode().equals(networkCode)) {
                return network;
            }
        }
        return null;
    }

    /**
     * Obtain the customer registration id from the operationResult
     *
     * @param operationResult the result that may contain the customer registration id
     * @return the customer registration id or null if not found
     */
    public static String getCustomerRegistrationId(OperationResult operationResult) {
        if (operationResult == null) {
            return null;
        }
        Redirect redirect = operationResult.getRedirect();
        if (redirect == null) {
            return null;
        }
        return getParameterValue("customerRegistrationId", redirect.getParameters());
    }


    /**
     * Put ProviderParameters requests into this operation.
     * If a request with the code and type is already stored, it will be replaced with the new request.
     *
     * @param providerRequests list of requests to be put into this operation
     */
    public static void putProviderRequests(final OperationData operationData, final List<ProviderParameters> providerRequests) {
        List<ProviderParameters> list = operationData.getProviderRequests();
        if (list == null) {
            list = new ArrayList<>();
            operationData.setProviderRequests(list);
        }
        for (ProviderParameters request : providerRequests) {
            int index = getProviderRequestIndex(operationData, request);
            if (index == -1) {
                list.add(request);
            } else {
                list.set(index, request);
            }
        }
    }

    /**
     * get the index of the provider request from the operationData, if the provider request was not found than return -1
     *
     * @param operationData in which the provider request should be found
     * @param providerRequest to be found in the operationData
     * @return the index of the providerRequest or -1 if not found
     */
    public static int getProviderRequestIndex(final OperationData operationData, final ProviderParameters providerRequest) {
        List<ProviderParameters> list = operationData.getProviderRequests();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                ProviderParameters parameters = list.get(i);
                if ((Objects.equals(parameters.getProviderCode(), providerRequest.getProviderCode())) &&
                    (Objects.equals(parameters.getProviderType(), providerRequest.getProviderType()))) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Get the provider parameter value from the operation result
     *
     * @param key of the parameter
     * @param result the operation result
     * @return value of the parameter or null if not found
     */
    public static String getProviderParameterValue(String key, OperationResult result) {
        if (result == null) {
            return null;
        }
        ProviderParameters parameters = result.getProviderResponse();
        if (parameters == null) {
            return null;
        }
        List<Parameter> params = parameters.getParameters();
        if (params == null) {
            return null;
        }
        for (Parameter p : params) {
            if (p.getName().equals(key)) {
                return p.getValue();
            }
        }
        return null;
    }

    /**
     * Get the provider code from the operation result
     *
     * @param result the operation result
     * @return the provider code or null if not found
     */
    public static String getProviderCode(OperationResult result) {
        if (result == null) {
            return null;
        }
        ProviderParameters parameters = result.getProviderResponse();
        if (parameters == null) {
            return null;
        }
        return parameters.getProviderCode();
    }

    /**
     * Check if the redirect type exists in the OperationResult
     *
     * @param operationResult contains the Redirect object
     * @param redirectType to match the redirect type to
     * @return true when it is a match, false otherwise
     */
    public static boolean containsRedirectType(OperationResult operationResult, String redirectType) {
        if (operationResult == null) {
            return false;
        }
        Redirect redirect = operationResult.getRedirect();
        return (redirect != null) && (Objects.equals(redirectType, redirect.getType()));
    }

    /**
     * Return an empty list when the provided list is null.
     *
     * @param list to be checked if null
     * @return the list if not null, else return an empty list
     */
    public static <T> List<T> emptyListIfNull(List<T> list) {
        return list == null ? Collections.emptyList() : list;
    }

    /**
     * Return an empty map when the provided map is null.
     *
     * @param map to be checked if null
     * @return the map if not null, else return an empty map
     */
    public static <K, V> Map<K, V> emptyMapIfNull(Map<K, V> map) {
        return map == null ? Collections.emptyMap() : map;
    }

    /**
     * Set the test Id to the view with the proper formatting understood by the automated UI tests.
     *
     * @param view in which the tag should be set
     * @param type the type of the View, i.e. widget, networkcard.
     * @param name the name of the view i.e. holderName
     */
    public static void setTestId(View view, String type, String name) {
        view.setContentDescription(type + "." + name);
    }
}
