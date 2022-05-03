/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.model;

/**
 * Class holding the configuration for this PaymentService
 */
public final class ButtonConfig {

    private final String labelKey;
    private final int layoutResourceId;

    public ButtonConfig(final String labelKey, final int layoutResourceId) {
        this.labelKey = labelKey;
        this.layoutResourceId = layoutResourceId;
    }

    public ButtonConfig(final String labelKey) {
        this(labelKey, 0);
    }

    public String getLabelKey() {
        return labelKey;
    }

    public int getLayoutResourceId() {
        return layoutResourceId;
    }
}
