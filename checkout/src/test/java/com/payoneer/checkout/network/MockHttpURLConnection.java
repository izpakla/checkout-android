package com.payoneer.checkout.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MockHttpURLConnection extends HttpURLConnection {

    private OutputStream outputStream = new ByteArrayOutputStream(0);

    private InputStream inputStream = null;

    private InputStream errorStream = null;

    private String contentType = null;

    private final Map<String, List<String>> headers = new LinkedHashMap<>();

    /**
     * @param u the URL or {@code null} for none
     */
    public MockHttpURLConnection(URL u) {
        super(u);
    }

    @Override
    public void disconnect() {
    }

    @Override
    public boolean usingProxy() {
        return false;
    }

    @Override
    public void connect() {
    }

    @Override
    public int getResponseCode() {
        return responseCode;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        if (outputStream != null) {
            return outputStream;
        }
        return super.getOutputStream();
    }

    public void setErrorStream(InputStream is) {
        if (is != null){
            if (errorStream == null) {
                errorStream = is;
            }
        }
    }

    public void setContentType(final String contenttype) {
        if (contenttype != null) {
            if (contentType == null) {
                this.contentType = contenttype;
            }
        }
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (responseCode < 400) {
            return inputStream;
        }
        throw new IOException();
    }

    @Override
    public InputStream getErrorStream() {
        return errorStream;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public Map<String, List<String>> getHeaderFields() {
        return headers;
    }

    @Override
    public String getHeaderField(String name) {
        List<String> values = headers.get(name);
        return values == null ? null : values.get(0);
    }
}