/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.payment.googlepayadyen;

import com.payoneer.checkout.ui.screen.ProgressView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Fragment to show the Payment Session
 */
public class GooglePayAdyenFragment extends Fragment {

    public GooglePayAdyenFragment() {
    }

    public static GooglePayAdyenFragment newInstance() {
        GooglePayAdyenFragment fragment = new GooglePayAdyenFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_googlepayadyen, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initProgressView(view);
    }

    private void initProgressView(final View view) {
        ProgressView progressView = new ProgressView(view.findViewById(R.id.layout_progress));
        progressView.setLabels("GooglePay", "Adyen");
        progressView.setVisible(true);
    }
}