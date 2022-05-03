/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.widget;

import com.payoneer.checkout.R;
import com.payoneer.checkout.ui.model.ButtonConfig;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Widget for showing the Submit (pay) button
 */
public final class ButtonWidget extends FormWidget {

    private Button button;
    private ButtonConfig configuration;
    private int layoutResId;

    public ButtonWidget(final String category, final String name, final int layoutResId) {
        super(category, name);
        this.layoutResId = layoutResId;
    }

    @Override
    public View inflate(ViewGroup parent) {
        inflateWidgetView(parent, (layoutResId == 0) ? R.layout.widget_button : layoutResId);
        button = widgetView.findViewById(R.id.button);

        setClickListener(button != null ? button : widgetView);
        if (button != null) {
            setClickListener(button);
        }
        return widgetView;
    }

    private void setClickListener(final View view) {
        widgetView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onActionClicked();
            }
        });
    }

    public void onBind(final String label) {
        if (button != null) {
            button.setText(label);
        }
    }
}
