/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.network;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import com.payoneer.checkout.core.PaymentException;

import androidx.test.core.app.ApplicationProvider;

/**
 * The type List connection test.
 */
@RunWith(RobolectricTestRunner.class)
public class ListConnectionTest {

    private final static String HEADER_USER_AGENT = "User-Agent";

    /**
     * Create payment session invalid baseUrl
     *
     * @throws PaymentException the network exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void createPaymentSession_invalidBaseUrl_IllegalArgumentException() throws PaymentException {
        ListConnection conn = createListConnection();
        conn.createPaymentSession(null, "auth123", "{}");
    }

    /**
     * Create payment session invalid authorization
     *
     * @throws PaymentException the network exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void createPaymentSession_invalidAuthorization_IllegalArgumentException() throws PaymentException {
        ListConnection conn = createListConnection();
        conn.createPaymentSession(createTestURL(), null, "{}");
    }

    /**
     * Create payment session invalid list data
     *
     * @throws PaymentException the network exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void createPaymentSession_invalidListData_IllegalArgumentException() throws PaymentException {
        ListConnection conn = createListConnection();
        conn.createPaymentSession(createTestURL(), "auth123", "");
    }

    /**
     * Gets list result invalid url
     *
     * @throws PaymentException the network exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void getListResult_invalidURL_IllegalArgumentException() throws PaymentException {
        ListConnection conn = createListConnection();
        conn.getListResult(null);
    }

    @Test
    public void createPostPaymentException_nullErrorStream_returnPaymentException() throws IOException {
        ListConnection conn = createListConnection();
        HttpURLConnection connection = conn.createPostConnection(createTestURL());
        PaymentException paymentException = conn.createPaymentException(2, connection);
        assertEquals(paymentException.getMessage(), "Received HTTP statusCode: " + 2 + "from the Payment API");
    }

    @Test
    public void createDeletePaymentException_nullErrorStream_returnPaymentException() throws IOException {
        ListConnection conn = createListConnection();
        HttpURLConnection connection = conn.createDeleteConnection(createTestURL());
        PaymentException paymentException = conn.createPaymentException(2, connection);
        assertEquals(paymentException.getMessage(), "Received HTTP statusCode: " + 2 + "from the Payment API");
    }

    @Test
    public void initialiseConnectionTwice_userAgentNotNull() throws IOException {
        ListConnection conn = createListConnection();
        BaseConnection.userAgent = null;
        HttpURLConnection connection = conn.createGetConnection(createTestURL());
        assertNull(connection.getRequestProperty(HEADER_USER_AGENT));
    }

    @Test
    public void withnonNullErrorStream_returnPaymentException() {
        ListConnection conn = createListConnection();
        MockHttpURLConnection connection = new MockHttpURLConnection(createTestURL());
        byte[] errorStream = { 1, 2 };
        connection.setErrorStream(new ByteArrayInputStream(errorStream));
        connection.setContentType("application/json");
        PaymentException paymentException = conn.createPaymentException(2, connection);
        assertEquals(paymentException.getMessage(), "Received HTTP statusCode: " + 2 + "from the Payment API");
    }

    @Test
    public void withThrowable_createPaymentException() {
        ListConnection conn = createListConnection();
        PaymentException paymentException = conn.createPaymentException(new IllegalArgumentException("Some message"), false);
        assertEquals(paymentException.getCause().getMessage(), "Some message");
        assertFalse(paymentException.getNetworkFailure());
    }

    private ListConnection createListConnection() {
        ListConnection conn = new ListConnection();
        conn.initialize(ApplicationProvider.getApplicationContext());
        return conn;
    }

    private URL createTestURL() {
        URL url = null;
        try {
            url = new URL("http://localhost");
        } catch (MalformedURLException exception) {
            exception.printStackTrace();
        }
        return url;
    }
}
