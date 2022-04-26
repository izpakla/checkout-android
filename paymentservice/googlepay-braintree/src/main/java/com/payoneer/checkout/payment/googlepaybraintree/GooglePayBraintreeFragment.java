/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.payment.googlepaybraintree;

import static com.payoneer.checkout.payment.googlepaybraintree.GooglePayBraintreePaymentService.BRAINTREE_AUTHORIZATION;
import static com.payoneer.checkout.payment.googlepaybraintree.GooglePayBraintreePaymentService.GOOGLEPAY_REQUEST;
import static com.payoneer.checkout.payment.googlepaybraintree.GooglePayBraintreePaymentService.TAG;

import com.braintreepayments.api.BraintreeClient;
import com.braintreepayments.api.GooglePayClient;
import com.braintreepayments.api.GooglePayListener;
import com.braintreepayments.api.GooglePayRequest;
import com.braintreepayments.api.PaymentMethodNonce;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Fragment to show the GooglePay bottomsheet from Braintree
 */
public class GooglePayBraintreeFragment extends Fragment {

    private String braintreeAuthentication;
    private GooglePayClient googlePayClient;

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        showGooglePay();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void showGooglePay() {
        String braintreeAuthorization = requireArguments().getString(BRAINTREE_AUTHORIZATION);
        GooglePayRequest googlePayRequest = requireArguments().getParcelable(GOOGLEPAY_REQUEST);

        BraintreeClient braintreeClient = new BraintreeClient(requireContext(), braintreeAuthorization);
        googlePayClient = new GooglePayClient(this, braintreeClient);
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
        Log.i(TAG, "handleGooglepaySuccess: " + paymentMethodNonce);
    }

    private void handleOnGooglePayFailure(final Exception exception) {
        Log.i(TAG, "handleGooglepayFailure", exception);
    }
}