/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.screen.payment;

import static com.payoneer.checkout.localization.LocalizationKey.CHARGE_INTERRUPTED;
import static com.payoneer.checkout.localization.LocalizationKey.CHARGE_TEXT;
import static com.payoneer.checkout.localization.LocalizationKey.CHARGE_TITLE;

import com.google.android.material.snackbar.Snackbar;
import com.payoneer.checkout.R;
import com.payoneer.checkout.localization.Localization;
import com.payoneer.checkout.ui.screen.shared.ProgressView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

/**
 * Fragment for displaying the progress indicator while processing the payment
 */
public final class ProcessPaymentFragment extends Fragment {
    private ProgressView progressView;

    public ProcessPaymentFragment() {
        super(R.layout.fragment_process_payment);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initProgressView(view);
        initViewModels();

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showWarningMessage();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }

    private void initProgressView(final View view) {
        progressView = new ProgressView(view.findViewById(R.id.layout_progress));
    }

    private void showWarningMessage() {
        View view = getView();
        String message = Localization.translate(CHARGE_INTERRUPTED);
        if (view != null && message != null) {
            Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
        }
    }

    private void initViewModels() {
        ProcessPaymentViewModel viewModel = new ViewModelProvider(requireActivity()).get(ProcessPaymentViewModel.class);
        viewModel.showProcessPaymentProgress().observe(getViewLifecycleOwner(), contentEvent -> {
            Boolean visible = (contentEvent != null) ? contentEvent.getContentIfNotHandled() : null;
            if (visible != null) {
                progressView.setLabels(Localization.translate(CHARGE_TITLE), Localization.translate(CHARGE_TEXT));
                progressView.setVisible(visible);
            }
        });
    }
}