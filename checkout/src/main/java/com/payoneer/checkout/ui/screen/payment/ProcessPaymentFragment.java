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
import com.payoneer.checkout.ui.screen.ProgressView;
import com.payoneer.checkout.ui.widget.TextInputWidget;
import com.payoneer.checkout.util.ContentEvent;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

/**
 * Fragment for displaying the progress indicator while processing the payment
 */
public final class ProcessPaymentFragment extends Fragment {
    private ProgressView progressView;

    public ProcessPaymentFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_process_payment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initProgressView(view);
        initObservers();

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
        progressView.setLabels(Localization.translate(CHARGE_TITLE), Localization.translate(CHARGE_TEXT));
    }

    private void showWarningMessage() {
        View view = getView();
        String message = Localization.translate(CHARGE_INTERRUPTED);
        if (view != null && message != null) {
            Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
        }
    }

    private void initObservers() {
        ProcessPaymentViewModel viewModel = new ViewModelProvider(requireActivity()).get(ProcessPaymentViewModel.class);
        viewModel.showProgress.observe(getViewLifecycleOwner(), contentEvent -> {
            Boolean visible = (contentEvent != null) ? contentEvent.getContentIfNotHandled() : null;
            if (visible != null) {
                progressView.setVisible(visible);
            }
        });
    }
}