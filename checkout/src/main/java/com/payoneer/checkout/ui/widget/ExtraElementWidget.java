/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.widget;

import static com.payoneer.checkout.model.CheckboxMode.FORCED;
import static com.payoneer.checkout.model.CheckboxMode.FORCED_DISPLAYED;
import static com.payoneer.checkout.model.CheckboxMode.REQUIRED;
import static com.payoneer.checkout.model.CheckboxMode.REQUIRED_PRESELECTED;

import java.util.Objects;

import com.payoneer.checkout.model.Checkbox;
import com.payoneer.checkout.model.CheckboxMode;
import com.payoneer.checkout.model.ExtraElement;

import android.view.View;

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
        String mode;
        if (checkbox != null) {
            mode = extraElement.getCheckbox().getMode();
            requiredMessage = checkbox.getRequiredMessage();
        } else {
            mode = CheckboxMode.NONE;
        }
        super.onBind(mode, extraElement.getLabel(), requiredMessage);
        if (Objects.equals(mode, FORCED) || Objects.equals(mode, FORCED_DISPLAYED)){
            switchView.setEnabled(false);
        }
    }
}
