/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.widget;

import android.view.View;

import com.payoneer.checkout.markdown.MarkdownSpannableStringBuilder;
import com.payoneer.checkout.model.CheckboxMode;
import com.payoneer.checkout.model.ExtraElement;

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
        String mode = (extraElement.getCheckbox() != null) ? extraElement.getCheckbox().getMode() : CheckboxMode.NONE;
        labelView.setText(MarkdownSpannableStringBuilder.createFromText(extraElement.getLabel()));
        switch (mode) {
            case CheckboxMode.OPTIONAL:
            case CheckboxMode.REQUIRED:
                setVisible(true);
                switchView.setVisibility(View.VISIBLE);
                switchView.setChecked(false);
                break;
            case CheckboxMode.OPTIONAL_PRESELECTED:
                setVisible(true);
                switchView.setVisibility(View.VISIBLE);
                switchView.setChecked(true);
                break;
            case CheckboxMode.FORCED_DISPLAYED:
                setVisible(true);
                switchView.setEnabled(false);
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
