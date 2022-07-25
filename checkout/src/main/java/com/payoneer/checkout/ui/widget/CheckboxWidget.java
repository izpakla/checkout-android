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

import java.util.Objects;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.payoneer.checkout.R;
import com.payoneer.checkout.markdown.MarkdownSpannableStringBuilder;
import com.payoneer.checkout.model.CheckboxMode;
import com.payoneer.checkout.payment.PaymentInputValues;

import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Widget for showing the CheckBox input element
 */
public class CheckboxWidget extends FormWidget {

    SwitchMaterial switchView;
    TextView labelView;
    TextView errorView;
    String mode;
    String requiredMessage;

    public CheckboxWidget(String category, String name) {
        super(category, name);
    }

    @Override
    public View inflate(ViewGroup parent) {
        inflateWidgetView(parent, R.layout.widget_checkbox);
        labelView = widgetView.findViewById(R.id.label_checkbox);
        errorView = widgetView.findViewById(R.id.error_view);
        switchView = widgetView.findViewById(R.id.switch_checkbox);
        labelView.setMovementMethod(LinkMovementMethod.getInstance());

        switchView.setOnClickListener((view -> {
            handleSwitchClicked(switchView.isChecked());
        }));
        return widgetView;
    }

    private void handleSwitchClicked(final boolean isChecked) {
        showRequiredMessage(!isChecked && (Objects.equals(mode, CheckboxMode.REQUIRED) || Objects.equals(mode, CheckboxMode.REQUIRED_PRESELECTED)));
        handleForcedCheckBoxClick(isChecked);
    }

    @Override
    public void putValue(final PaymentInputValues inputValues) {
        inputValues.putBooleanValue(category, name, switchView.isChecked());
    }

    @Override
    public boolean validate() {
        boolean checked = switchView.isChecked();
        switch (mode) {
            case REQUIRED:
            case REQUIRED_PRESELECTED:
                if (!checked) {
                    showRequiredMessage(true);
                    return false;
                }
                break;
            case FORCED:
            case FORCED_DISPLAYED:
                return checked;
        }
        return true;
    }

    private void showRequiredMessage(final boolean show) {
        if (show && !TextUtils.isEmpty(requiredMessage)) {
            errorView.setVisibility(View.VISIBLE);
            errorView.setText(requiredMessage);
        } else {
            errorView.setVisibility(View.GONE);
        }
    }

    private void handleForcedCheckBoxClick(final boolean isChecked) {
        if ((Objects.equals(mode, CheckboxMode.FORCED) || Objects.equals(mode, CheckboxMode.FORCED_DISPLAYED))) {
            if (!isChecked) {
                switchView.setChecked(true);
            }
        }
        presenter.showForcedCheckboxDialog();
    }

    /**
     * Bind this CheckboxWidget to the mode and label.
     * For now the required and required preselected are handled the same as the optional modes.
     * This is because there is no requireMsg yet defined for checkboxes in the localization files.
     *
     * @param mode checkbox mode
     * @param label shown to the user
     */
    public void onBind(String mode, String label, String requiredMessage) {
        this.mode = mode;
        labelView.setText(MarkdownSpannableStringBuilder.createFromText(label));
        this.requiredMessage = requiredMessage;

        showRequiredMessage(false);
        switch (mode) {
            case CheckboxMode.OPTIONAL:
            case REQUIRED:
                setVisible(true);
                switchView.setVisibility(View.VISIBLE);
                switchView.setChecked(false);
                break;
            case CheckboxMode.OPTIONAL_PRESELECTED:
            case CheckboxMode.REQUIRED_PRESELECTED:
            case CheckboxMode.FORCED_DISPLAYED:
            case CheckboxMode.FORCED:
                setVisible(true);
                switchView.setVisibility(View.VISIBLE);
                switchView.setChecked(true);
                break;
            default:
                setVisible(false);
                switchView.setVisibility(View.GONE);
                switchView.setChecked(false);
        }
    }
}
