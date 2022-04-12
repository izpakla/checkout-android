/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.screen.list;

import com.payoneer.checkout.R;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

/**
 * Class managing showing a ProgressBar with optional labels.
 */
class ProgressView {

    private final TextView textHeader;
    private final TextView textInfo;
    private final View view;

    /**
     * Construct a new loading view given the parent view that holds the Views for the loading animations
     *
     * @param view the root view containing the progress views and layouts
     */
    ProgressView(View view) {
        this.view = view;
        textHeader = view.findViewById(R.id.text_header);
        textInfo = view.findViewById(R.id.text_info);
    }

    /**
     * Set the labels shown under the progressbar.
     *
     * @param header label
     * @param info label
     */
    public void setLabels(String header, String info) {
        if (!TextUtils.isEmpty(header)) {
            textHeader.setText(header);
            textHeader.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(header)) {
            textInfo.setText(info);
            textInfo.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Show the progress bar with optional title and info labels
     *
     * @param visible when true, show the loading animation, hide it otherwise
     */
    public void setVisible(boolean visible) {
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}
