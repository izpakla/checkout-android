/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.screen.payment;

import com.payoneer.checkout.R;
import com.payoneer.checkout.ui.screen.ProgressView;
import com.payoneer.checkout.util.ContentEvent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

/**
 * Fragment for displaying the progress indicator
 */
public class ProcessPaymentFragment extends Fragment {

    private ProcessPaymentViewModel paymentViewModel;
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
    }

    private void initProgressView(final View view) {
        progressView = new ProgressView(view.findViewById(R.id.layout_progress));
    }

    private void initObservers() {
        paymentViewModel = new ViewModelProvider(requireActivity()).get(ProcessPaymentViewModel.class);
        paymentViewModel.showProgress.observe(getViewLifecycleOwner(), new Observer<ContentEvent>() {
            @Override
            public void onChanged(@Nullable ContentEvent contentEvent) {
                Boolean visible = (Boolean) contentEvent.getContentIfNotHandled();
                if (visible != null) {
                    progressView.setVisible(visible);
                }
            }
        });
    }
}