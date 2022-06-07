/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.screen.shared;

import com.payoneer.checkout.R;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

/**
 * Class managing showing a ProgressBar with optional labels.
 */
public final class ProgressView {

    private final TextView textHeader;
    private final TextView textInfo;
    private final View view;

    /**
     * Construct a new loading view given the parent view that holds the Views for the loading animations
     *
     * @param view the root view containing the progress views and layouts
     */
    public ProgressView(View view) {
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
    public void setLabels(final String header, final String info) {
        if (!TextUtils.isEmpty(header)) {
            textHeader.setText(header);
            textHeader.setVisibility(View.VISIBLE);
        } else {
            textHeader.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(info)) {
            textInfo.setText(info);
            textInfo.setVisibility(View.VISIBLE);
        } else {
            textInfo.setVisibility(View.GONE);
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

    public void setProgressSettings(final ProgressSettings settings) {
        setVisible(settings.visible);
        setLabels(settings.header, settings.info);
    }
}
