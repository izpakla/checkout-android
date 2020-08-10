/*
 * Copyright (c) 2019 optile GmbH
 * https://www.optile.net
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package net.optile.payment.ui.widget;

import static com.google.android.material.textfield.TextInputLayout.END_ICON_CUSTOM;

import android.view.View;
import net.optile.payment.R;
import net.optile.payment.localization.Localization;
import net.optile.payment.localization.LocalizationKey;
import net.optile.payment.model.InputElement;
import net.optile.payment.ui.widget.input.EditTextInputModeFactory;

/**
 * Widget for showing the verification code input
 */
public final class VerificationCodeWidget extends InputLayoutWidget {

    /**
     * Construct a new VerificationCodeWidget
     *
     * @param name name identifying this widget
     * @param rootView the root view of this input
     */
    public VerificationCodeWidget(String name, View rootView) {
        super(name, rootView);
        setEndIcon(END_ICON_CUSTOM, R.drawable.ic_tooltip);
    }

    /**
     * Bind this verification code widget to the InputElement.
     *
     * @param code of the payment network this widget belongs to
     * @param element to bind this widget to
     */
    public void onBind(String code, InputElement element) {
        int maxLength = presenter.getMaxLength(code, name);
        setTextInputMode(EditTextInputModeFactory.createMode(maxLength, element));
        setValidation();
        setLabel(Localization.translateAccountLabel(code, name));
        setHelperText(Localization.translate(code, LocalizationKey.VERIFICATIONCODE_SPECIFIC_PLACEHOLDER));
    }

    void handleOnEndIconClicked() {
        presenter.onHintClicked(name);
    }
}