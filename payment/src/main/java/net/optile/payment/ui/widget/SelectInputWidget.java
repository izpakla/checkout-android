/*
 * Copyright(c) 2012-2018 optile GmbH. All Rights Reserved.
 * https://www.optile.net
 *
 * This software is the property of optile GmbH. Distribution  of  this
 * software without agreement in writing is strictly prohibited.
 *
 * This software may not be copied, used or distributed unless agreement
 * has been received in full.
 */

package net.optile.payment.ui.widget;

import java.util.List;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import net.optile.payment.R;
import net.optile.payment.core.PaymentException;
import net.optile.payment.form.Charge;
import net.optile.payment.model.InputElement;
import net.optile.payment.model.SelectOption;
import net.optile.payment.util.PaymentUtils;

/**
 * Class for handling the Select input type
 */
public final class SelectInputWidget extends FormWidget {

    private final InputElement element;
    private final Spinner spinner;
    private final TextView label;

    /**
     * Construct a new SelectInputWidget
     *
     * @param name identifying this widget
     * @param rootView the root view of this input
     * @param element the InputElement this widget is displaying
     */
    public SelectInputWidget(String name, View rootView, InputElement element) {
        super(name, rootView);
        this.element = element;
        spinner = rootView.findViewById(R.id.input_spinner);
        label = rootView.findViewById(R.id.input_label);
        label.setText(element.getLabel());
        initSpinner();
    }

    public void putValue(Charge charge) throws PaymentException {
        SpinnerItem selected = (SpinnerItem) spinner.getSelectedItem();
        if (selected != null) {
            charge.putValue(element.getName(), selected.value);
        }
    }

    private void initSpinner() {

        List<SelectOption> options = element.getOptions();
        if (options == null || options.size() == 0) {
            return;
        }
        ArrayAdapter<SpinnerItem> adapter = new ArrayAdapter<>(rootView.getContext(), R.layout.spinner_item);
        int selIndex = 0;
        SelectOption option;

        for (int i = 0, e = options.size(); i < e; i++) {
            option = options.get(i);
            adapter.add(new SpinnerItem(option.getLabel(), option.getValue()));

            if (PaymentUtils.isTrue(option.getSelected())) {
                selIndex = i;
            }
        }
        spinner.setAdapter(adapter);
        spinner.setSelection(selIndex);
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

    class SpinnerItem {
        final String label;
        final String value;

        SpinnerItem(String label, String value) {
            this.label = label;
            this.value = value;
        }

        public String toString() {
            return label;
        }
    }
}
