/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.util;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import android.text.TextUtils;

/**
 * Class with helper methods for Gson
 */
public final class GsonHelper {

    private final Gson gson;

    private GsonHelper() {
        this.gson = new GsonBuilder().create();
    }

    /**
     * Get the instance of this GsonHelper
     *
     * @return the instance of this GsonHelper
     */
    public static GsonHelper getInstance() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * This method serializes the specified object into its equivalent Json representation.
     *
     * @return Json representation of src
     */
    public String toJson(Object src) {
        return src == null ? null : gson.toJson(src);
    }

    public <T> T fromJson(String json, Class<T> classOfT) throws JsonSyntaxException {
        return TextUtils.isEmpty(json) ? null : gson.fromJson(json, classOfT);
    }

    public <T> T fromJson(String json, Type type) throws JsonSyntaxException {
        return TextUtils.isEmpty(json) ? null : gson.fromJson(json, type);
    }

    private static class InstanceHolder {
        static final GsonHelper INSTANCE = new GsonHelper();

    }
}
