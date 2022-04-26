/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.payment.googlepaybraintree;

import static com.payoneer.checkout.payment.googlepaybraintree.GooglePayBraintreePaymentService.BRAINTREE_AUTHORIZATION;
import static com.payoneer.checkout.payment.googlepaybraintree.GooglePayBraintreePaymentService.TAG;

import android.content.Intent;
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

    public GooglePayBraintreeFragment() {
    }

    public static GooglePayBraintreeFragment newInstance(final String braintreeAuthorization) {
        GooglePayBraintreeFragment fragment = new GooglePayBraintreeFragment();
        Bundle args = new Bundle();
        args.putString(BRAINTREE_AUTHORIZATION, braintreeAuthorization);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_googlepaybraintree, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        String braintreeAuthKey = requireArguments().getString(BRAINTREE_AUTHORIZATION);
        Log.i(TAG, "braintree: " + braintreeAuthKey);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}