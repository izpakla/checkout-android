/*
 *
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 *
 */

package com.payoneer.checkout.util;

public class Resource<T> {

    public final static int SUCCESS = 0x00;
    public final static int ERROR = 0x01;
    public final static int LOADING = 0x02;

    private final T data;
    private final int status;
    private final String message;

    private Resource(final int status, final T data, final String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static <T> Resource<T> success(final T data) {
        return new Resource(SUCCESS, data, null);
    }

    public static Resource error(final String message) {
        return new Resource(ERROR, null, message);
    }

    public static Resource loading() {
        return new Resource(LOADING, null, null);
    }

    public int getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}
