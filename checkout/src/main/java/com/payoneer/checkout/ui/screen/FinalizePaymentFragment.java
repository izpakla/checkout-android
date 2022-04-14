/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.screen;

import static com.payoneer.checkout.localization.LocalizationKey.CHARGE_INTERRUPTED;
import static com.payoneer.checkout.localization.LocalizationKey.CHARGE_TEXT;
import static com.payoneer.checkout.localization.LocalizationKey.CHARGE_TITLE;

import com.google.android.material.snackbar.Snackbar;
import com.payoneer.checkout.R;
import com.payoneer.checkout.localization.Localization;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Fragment to show the full screen in progress animation
 */
public class FinalizePaymentFragment extends Fragment {

    public FinalizePaymentFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_finalize_payment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initProgressView(view);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showWarningMessage();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }

    private void showWarningMessage() {
        Snackbar.make(getView(), Localization.translate(CHARGE_INTERRUPTED), Snackbar.LENGTH_LONG).show();
    }

    private void initProgressView(final View view) {
        ProgressView progressView = new ProgressView(view.findViewById(R.id.layout_progress));
        progressView.setLabels(Localization.translate(CHARGE_TITLE), Localization.translate(CHARGE_TEXT));
        progressView.setVisible(true);
    }
}