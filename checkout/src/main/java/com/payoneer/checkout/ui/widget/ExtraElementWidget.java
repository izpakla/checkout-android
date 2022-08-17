/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.widget;

import com.payoneer.checkout.model.Checkbox;
import com.payoneer.checkout.model.CheckboxMode;
import com.payoneer.checkout.model.ExtraElement;
import com.payoneer.checkout.util.PaymentUtils;

/**
 * Widget for showing the ExtraElement element
 */
public class ExtraElementWidget extends CheckboxWidget {

    public ExtraElementWidget(String category, String name) {
        super(category, name);
    }

    /**
     * Bind this ExtraElementWidget to the ExtraElement
     *
     * @param extraElement containing the label and optional checkbox
     */
    public void onBind(ExtraElement extraElement) {
        Checkbox checkbox = extraElement.getCheckbox();
        String mode = (checkbox != null) ? extraElement.getCheckbox().getMode() : CheckboxMode.NONE;
        super.onBind(mode, extraElement.getLabel(), PaymentUtils.getCheckboxRequiredMessage(extraElement));
    }
}
