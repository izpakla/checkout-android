/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.widget;

import static com.payoneer.checkout.model.RegistrationType.FORCED;
import static com.payoneer.checkout.model.RegistrationType.FORCED_DISPLAYED;
import static com.payoneer.checkout.model.RegistrationType.OPTIONAL;
import static com.payoneer.checkout.model.RegistrationType.OPTIONAL_PRESELECTED;

import java.util.Objects;

import com.payoneer.checkout.localization.Localization;
import com.payoneer.checkout.payment.PaymentInputValues;
import com.payoneer.checkout.ui.model.RegistrationOptions;
import com.payoneer.checkout.ui.model.RegistrationOptions.RegistrationOption;

import android.view.View;

/**
 * Widget for showing the RegistrationOptions, e.g. allowRecurrence and autoRegistration
 */
public class RegistrationWidget extends CheckboxWidget {

    private RegistrationOptions registrationOptions;

    public RegistrationWidget(String category, String name) {
        super(category, name);
    }

    @Override
    public void putValue(final PaymentInputValues inputValues) {
        for (RegistrationOption option : registrationOptions.getRegistrationOptions()) {
            putRegistrationValue(inputValues, option);
        }
    }

    private void putRegistrationValue(final PaymentInputValues inputValues, RegistrationOption option) {
        boolean value;
        switch (option.getType()) {
            case FORCED:
            case FORCED_DISPLAYED:
                value = true;
                break;
            case OPTIONAL:
            case OPTIONAL_PRESELECTED:
                value = switchView.isChecked();
                break;
            default:
                value = false;
        }
        inputValues.putBooleanValue(category, option.getName(), value);
    }

    /**
     * Bind this RegistrationWidget to the RegistrationOption
     *
     * @param registrationOptions containing the registration options
     */
    public void onBind(RegistrationOptions registrationOptions) {
        this.registrationOptions = registrationOptions;
        String label = Localization.translate(registrationOptions.getLabelKey());
        super.onBind(registrationOptions.getCheckboxMode(), label, null);
        if (Objects.equals(mode, FORCED_DISPLAYED)) {
            switchView.setVisibility(View.GONE);
        }
    }
}
