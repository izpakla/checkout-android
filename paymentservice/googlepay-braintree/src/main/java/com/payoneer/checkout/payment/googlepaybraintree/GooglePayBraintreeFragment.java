/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.payment.googlepaybraintree;

import static com.payoneer.checkout.payment.googlepaybraintree.GooglePayBraintreePaymentService.BRAINTREE_AUTHORIZATION;
import static com.payoneer.checkout.payment.googlepaybraintree.GooglePayBraintreePaymentService.BRAINTREE_ERROR;
import static com.payoneer.checkout.payment.googlepaybraintree.GooglePayBraintreePaymentService.BRAINTREE_NONCE;
import static com.payoneer.checkout.payment.googlepaybraintree.GooglePayBraintreePaymentService.GOOGLEPAY_REQUEST;

import com.braintreepayments.api.BraintreeClient;
import com.braintreepayments.api.GooglePayClient;
import com.braintreepayments.api.GooglePayListener;
import com.braintreepayments.api.GooglePayRequest;
import com.braintreepayments.api.PaymentMethodNonce;
import com.payoneer.checkout.payment.PaymentServiceViewModel;
import com.payoneer.checkout.ui.screen.shared.ProgressView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

/**
 * Fragment to show the GooglePay bottomsheet from Braintree
 */
public class GooglePayBraintreeFragment extends Fragment {
    private PaymentServiceViewModel viewModel;
    private ProgressBar progressBar;

    public GooglePayBraintreeFragment() {
    }

    public static GooglePayBraintreeFragment newInstance(final Bundle arguments) {
        GooglePayBraintreeFragment fragment = new GooglePayBraintreeFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_googlepaybraintree, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPause() {
        super.onPause();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        progressBar = view.findViewById(R.id.progressbar);
        viewModel = new ViewModelProvider(requireActivity()).get(PaymentServiceViewModel.class);
        showGooglePay();
    }

    private void showGooglePay() {
        String braintreeAuthorization = requireArguments().getString(BRAINTREE_AUTHORIZATION);
        GooglePayRequest googlePayRequest = requireArguments().getParcelable(GOOGLEPAY_REQUEST);

        BraintreeClient braintreeClient = new BraintreeClient(requireContext(), braintreeAuthorization);
        GooglePayClient googlePayClient = new GooglePayClient(this, braintreeClient);
        googlePayClient.setListener(new GooglePayListener() {
            @Override
            public void onGooglePaySuccess(@NonNull final PaymentMethodNonce paymentMethodNonce) {
                handleOnGooglePaySuccess(paymentMethodNonce);
            }

            @Override
            public void onGooglePayFailure(@NonNull final Exception error) {
                handleOnGooglePayFailure(error);
            }
        });
        googlePayClient.requestPayment(requireActivity(), googlePayRequest);
    }

    private void handleOnGooglePaySuccess(final PaymentMethodNonce paymentMethodNonce) {
        Bundle attributes = new Bundle();
        attributes.putParcelable(BRAINTREE_NONCE, paymentMethodNonce);
        viewModel.onFragmentResult(attributes);
    }

    private void handleOnGooglePayFailure(final Exception exception) {
        Bundle attributes = new Bundle();
        attributes.putSerializable(BRAINTREE_ERROR, exception);
        viewModel.onFragmentResult(attributes);
    }
}