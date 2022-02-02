/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.model;

import java.util.Date;

/**
 * Customer data what should be used to pre-fill payment form.
 */
public class CustomerFormData {
    /** optional */
    private Date birthday;

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(final Date birthday) {
        this.birthday = birthday;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("CustomerFormData [");
        if (birthday != null) {
            builder.append("birthday=").append(birthday);
        }
        builder.append("]");
        return builder.toString();
    }
}
