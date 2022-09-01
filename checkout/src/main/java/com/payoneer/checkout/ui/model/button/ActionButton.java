/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.model.button;

import com.payoneer.checkout.localization.Localization;

public class ActionButton {

    private final String buttonKey;

    public ActionButton(final String buttonKey) {
        this.buttonKey = buttonKey;
    }

    public String getButtonLabel(String networkCode) {
        return Localization.translate(networkCode, buttonKey);
    }

}
