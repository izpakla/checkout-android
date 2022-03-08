/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.payoneer.checkout.core.PaymentException;
import com.payoneer.checkout.util.GsonHelper;

import android.content.res.Resources;

/**
 * The ResourceLoader class containing helper methods for loading group and validation raw json files.
 */
public final class ResourceLoader {

    /**
     * Load the payment group definition json file and return the map of PaymentGroups that should be used.
     * Each PaymentGroupItem code can be used as lookup key to find the corresponding PaymentGroup.
     *
     * @param res the System resources
     * @param resId id of the resource pointing to the json file
     * @return map of payment group objects.
     */
    public static Map<String, PaymentGroup> loadPaymentGroups(Resources res, int resId) throws PaymentException {

        try {
            String val = readRawResource(res, resId);
            Type listType = new TypeToken<ArrayList<PaymentGroup>>() { }.getType();
            List<PaymentGroup> groups = GsonHelper.getInstance().fromJson(val, listType);
            HashMap<String, PaymentGroup> map = new HashMap<>();

            for (PaymentGroup group : groups) {
                group.populate(map);
            }
            return map;
        } catch (IOException | JsonSyntaxException e) {
            throw new PaymentException(e);
        }
    }

    /**
     * Load the validation group definition json file and return a map of ValidationGroups.
     * The lookup key for each validation group is the code combined with merchant. String key = code + merchant.
     *
     * @param res the System resources
     * @param resId id of the resource pointing to the json file
     * @return the map of ValidationGroup objects
     */

    public static Map<String, ValidationGroup> loadValidations(Resources res, int resId) throws PaymentException {
        try {
            String val = readRawResource(res, resId);
            Type listType = new TypeToken<ArrayList<ValidationGroup>>() { }.getType();
            List<ValidationGroup> groups = GsonHelper.getInstance().fromJson(val, listType);
            Map<String, ValidationGroup> map = new HashMap<>();

            for (ValidationGroup group : groups) {
                map.put(group.getCode(), group);
            }
            return map;
        } catch (IOException | JsonSyntaxException e) {
            throw new PaymentException(e);
        }
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
}
