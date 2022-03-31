/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.network;

import android.net.Uri;
import android.text.TextUtils;

import com.google.gson.JsonParseException;
import com.payoneer.checkout.core.PaymentException;
import com.payoneer.checkout.model.ListResult;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Class implementing the communication with the List payment API
 * <p>
 * All requests in this class are blocking calls and should be
 * executed in a separate thread to avoid blocking the main application thread.
 * These methods are not thread safe and must not be called by different threads
 * at the same time.
 */
public final class ListConnection extends BaseConnection {

    /**
     * Create a new payment session through the Server Payment API. Remind this is not
     * a request mobile apps should be making as this call is normally executed
     * Merchant Server-side. This request will be removed later.
     *
     * @param listURL       the listURL of the Payment API
     * @param authorization the authorization header data
     * @param listData      the data containing the request body for the list request
     * @return the ListResult
     */
    public ListResult createPaymentSession(final URL listURL, final String authorization, final String listData)
            throws PaymentException {
        if (listURL == null) {
            throw new IllegalArgumentException("createPaymentSession - listURL cannot be null or empty");
        }
        if (TextUtils.isEmpty(authorization)) {
            throw new IllegalArgumentException("authorization cannot be null or empty");
        }
        if (TextUtils.isEmpty(listData)) {
            throw new IllegalArgumentException("listData cannot be null or empty");
        }

        HttpURLConnection conn = null;
        try {
            conn = createPostConnection(listURL);
            conn.setRequestProperty(HEADER_AUTHORIZATION, authorization);
            conn.setRequestProperty(HEADER_CONTENT_TYPE, VALUE_APP_JSON);
            conn.setRequestProperty(HEADER_ACCEPT, VALUE_APP_JSON);

            writeToOutputStream(conn, listData);
            conn.connect();
            final int rc = conn.getResponseCode();

            if (rc == HttpURLConnection.HTTP_OK) {
                return handleCreatePaymentSessionOk(readFromInputStream(conn));
            }
            throw createPaymentException(rc, conn);
        } catch (JsonParseException | MalformedURLException | SecurityException e) {
            throw createPaymentException(e, false);
        } catch (IOException e) {
            throw createPaymentException(e, true);
        } finally {
            close(conn);
        }
    }

    /**
     * Make a get request to the Payment API in order to
     * obtain the details of an active list session
     *
     * @param url the url pointing to the list
     * @return the NetworkResponse containing either an error or the ListResult
     */
    public ListResult getListResult(final URL url) throws PaymentException {
        if (url == null) {
            throw new IllegalArgumentException("getListResult - URL cannot be null");
        }
        HttpURLConnection conn = null;

        try {
            //We first call toURI then parse because toURI() returns a java.net.URI.
            // So we need to map to string and then parse into android.net.Uri
            final String requestUrl = Uri.parse(url.toURI().toString()).buildUpon()
                    .build().toString();

            conn = createGetConnection(requestUrl);
            conn.setRequestProperty(HEADER_CONTENT_TYPE, VALUE_APP_JSON);
            conn.setRequestProperty(HEADER_ACCEPT, VALUE_APP_JSON);

            conn.connect();
            final int rc = conn.getResponseCode();
            if (rc == HttpURLConnection.HTTP_OK) {
                return handleGetListResultOk(readFromInputStream(conn));
            }
            throw createPaymentException(rc, conn);
        } catch (JsonParseException | MalformedURLException | URISyntaxException | SecurityException e) {
            throw createPaymentException(e, false);
        } catch (IOException e) {
            throw createPaymentException(e, true);
        } finally {
            close(conn);
        }
    }


    /**
     * Handle the create new payment session OK state
     *
     * @param data the response data received from the API
     * @return the ListResult
     */
    private ListResult handleCreatePaymentSessionOk(final String data) throws JsonParseException {
        return gson.fromJson(data, ListResult.class);
    }

    /**
     * Handle get list result OK state
     *
     * @param data the response data received from the Payment API
     * @return the ListResult
     */
    private ListResult handleGetListResultOk(final String data) throws JsonParseException {
        return gson.fromJson(data, ListResult.class);
    }
}
