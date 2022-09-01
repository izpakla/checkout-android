/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.model.button;

import java.math.BigDecimal;

public class AmountActionButton extends ActionButton {

    private static final String AMOUNT_PLACEHOLDER = "${amount}";

    private final BigDecimal amount;
    private final String currency;

    public AmountActionButton(final String buttonKey, final BigDecimal amount, final String currency) {
        super(buttonKey);
        this.amount = amount;
        this.currency = currency;
    }

    @Override
    public String getButtonLabel(final String networkCode) {
        String amountLabel = String.valueOf(amount) + ' ' + currency;
        return super.getButtonLabel(networkCode).replace(AMOUNT_PLACEHOLDER, amountLabel);
    }
}
