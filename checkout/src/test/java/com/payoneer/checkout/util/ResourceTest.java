/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class ResourceTest {

    @Test
    public void success() {
        Integer data = new Integer(100);
        Resource<Integer> res = Resource.success(data);
        assertEquals(res.getStatus(), Resource.SUCCESS);
        assertEquals(data, res.getData());
        assertNull(res.getMessage());
    }

    @Test
    public void error() {
        String message = "Error message";
        Resource<String> res = Resource.error(message);
        assertEquals(res.getStatus(), Resource.ERROR);
        assertEquals(message, res.getMessage());
        assertNull(res.getData());
    }

    @Test
    public void loading() {
        Resource<String> res = Resource.loading();
        assertEquals(res.getStatus(), Resource.LOADING);
        assertNull(res.getMessage());
        assertNull(res.getData());
    }
}