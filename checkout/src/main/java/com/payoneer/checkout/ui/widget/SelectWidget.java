/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.widget;

import java.util.List;

import com.payoneer.checkout.R;
import com.payoneer.checkout.core.PaymentException;
import com.payoneer.checkout.form.Operation;
import com.payoneer.checkout.localization.Localization;
import com.payoneer.checkout.model.InputElement;
import com.payoneer.checkout.model.SelectOption;
import com.payoneer.checkout.util.PaymentUtils;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;

/**
 * Widget for handling the Select input type
 */
public final class SelectWidget extends FormWidget {

    private final Spinner spinner;
    private final TextView label;
    private final ArrayAdapter<SpinnerItem> adapter;

    /**
     * Construct a new SelectWidget
     *
     * @param name identifying this widget
     * @param rootView the root view of this input
     */
    public SelectWidget(String name, View rootView) {
        super(name, rootView);
        label = rootView.findViewById(R.id.input_label);
        adapter = new ArrayAdapter<>(rootView.getContext(), R.layout.spinner_item);

        spinner = rootView.findViewById(R.id.input_spinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                validate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putValue(Operation operation) throws PaymentException {
        SpinnerItem selected = (SpinnerItem) spinner.getSelectedItem();

        if (selected != null) {
            operation.putStringValue(name, selected.value);
        }
    }

    public void onBind(String code, InputElement element) {
        label.setText(Localization.translateAccountLabel(code, name));
        adapter.clear();
        List<SelectOption> options = element.getOptions();

        if (options == null || options.size() == 0) {
            return;
        }
        int selIndex = 0;
        SelectOption option;

        for (int i = 0, e = options.size(); i < e; i++) {
            option = options.get(i);
            String value = option.getValue();
            String label = Localization.translateAccountValue(code, name, value);
            adapter.add(new SpinnerItem(label, value));

            if (PaymentUtils.isTrue(option.getSelected())) {
                selIndex = i;
            }
        }
        spinner.setSelection(selIndex);
    }

    static class SpinnerItem {
        final String label;
        final String value;

        SpinnerItem(String label, String value) {
            this.label = label;
            this.value = value;
        }

        @NonNull
        public String toString() {
            return label;
        }
    }
}
