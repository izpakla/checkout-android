/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.network;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.payoneer.checkout.core.PaymentException;
import com.payoneer.checkout.localization.LocalizationHolder;
import com.payoneer.checkout.localization.MapLocalizationHolder;

import android.content.Context;

/**
 * Class implementing the communication with the payment API to load localization files
 * <p>
 * All requests in this class are blocking calls and should be
 * executed in a separate thread to avoid blocking the main application thread.
 * These methods are not thread safe and must not be called by different threads
 * at the same time.
 */
public final class LocalizationConnection extends BaseConnection {

    /**
     * Load the localization file given the URL.
     *
     * @param url containing the address of the remote language file
     * @return LocalizationFile object containing the language entries
     */
    public LocalizationHolder loadLocalization(URL url) throws PaymentException {
        if (url == null) {
            throw new IllegalArgumentException("url cannot be null");
        }
        HttpURLConnection conn = null;
        try {
            conn = createGetConnection(url);
            conn.setRequestProperty(HEADER_CONTENT_TYPE, VALUE_APP_JSON);
            conn.setRequestProperty(HEADER_ACCEPT, VALUE_APP_JSON);
            conn.connect();
            final int rc = conn.getResponseCode();
            if (rc == HttpURLConnection.HTTP_OK) {
                return handleLoadLocalizationOk(readFromInputStream(conn));
            }
            throw createPaymentException(rc, conn);
        } catch (JsonParseException | SecurityException e) {
            throw createPaymentException(e, false);
        } catch (IOException e) {
            throw createPaymentException(e, true);
        } finally {
            close(conn);
        }
    }

    /**
     * Handle get localizations ok
     *
     * @param data the response data received from the Payment API
     * @return the LocalizationHolder containing the localizations
     */
    private LocalizationHolder handleLoadLocalizationOk(final String data) throws JsonParseException {
        Map<String, String> map = gson.fromJson(data, new TypeToken<HashMap<String, String>>() {
        }.getType());
        return new MapLocalizationHolder(map);
    }
}
