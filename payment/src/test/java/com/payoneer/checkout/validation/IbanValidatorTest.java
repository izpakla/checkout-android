/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.payoneer.checkout.validation.IbanValidator;

public class IbanValidatorTest {

    @Test
    public void isValidIban() {
        assertTrue(IbanValidator.isValidIban("DE27100777770209299700"));
        assertTrue(IbanValidator.isValidIban("AT022050302101023600"));

        assertFalse(IbanValidator.isValidIban(null));
        assertFalse(IbanValidator.isValidIban(""));
        assertFalse(IbanValidator.isValidIban("AT1234567890123456"));
    }
}
