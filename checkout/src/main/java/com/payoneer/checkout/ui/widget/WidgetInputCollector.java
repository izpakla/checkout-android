/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.widget;

import com.payoneer.checkout.core.PaymentException;

/**
 * Interface for collection widget input values
 */
public interface WidgetInputCollector {

    /**
     * Put a boolean value into this Operation form.
     * Depending on the category and name of the value it will be added to the correct place in the Operation Json Object.
     *
     * @param category category the input value belongs to
     * @param name name identifying the value
     * @param value containing the value of the input
     */
    void putBooleanValue(String category, String name, boolean value) throws PaymentException;

    /**
     * Put a String value into this Operation form.
     * Depending on the category and name of the value it will be added to the correct place in the Operation Json Object.
     *
     * @param category category the input value belongs to
     * @param name name identifying the value
     * @param value containing the value of the input
     */
    void putStringValue(String category, String name, String value) throws PaymentException;
}
