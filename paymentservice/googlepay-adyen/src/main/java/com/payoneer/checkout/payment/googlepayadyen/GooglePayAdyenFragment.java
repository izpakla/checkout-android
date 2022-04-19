/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.payment.googlepayadyen;

import java.util.Optional;

import org.json.JSONObject;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;

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
 * Fragment to show the Payment Session
 */
public class GooglePayAdyenFragment extends Fragment {

    private final static int GOOGLEPAY_REQUEST_CODE = 1234;
    private PaymentsClient paymentsClient;

    public GooglePayAdyenFragment() {
    }

    public static GooglePayAdyenFragment newInstance() {
        GooglePayAdyenFragment fragment = new GooglePayAdyenFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("AAA", "onActivityResult: " + requestCode);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_googlepayadyen, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Wallet.WalletOptions walletOptions = new Wallet.WalletOptions.Builder().setEnvironment(WalletConstants.ENVIRONMENT_TEST).build();
        paymentsClient = Wallet.getPaymentsClient(requireActivity(), walletOptions);
    }

    @Override
    public void onResume() {
        super.onResume();
        showGooglePayWallet();

    }

    private void showGooglePayWallet() {
        final Optional<JSONObject> isReadyToPayJson = GooglePay.getIsReadyToPayRequest();
        if (!isReadyToPayJson.isPresent()) {
            Log.i("AAA", "IsReadyToPay JSON is not present");
            return;
        }
        IsReadyToPayRequest request = IsReadyToPayRequest.fromJson(isReadyToPayJson.get().toString());
        Task<Boolean> task = paymentsClient.isReadyToPay(request);
        task.addOnCompleteListener(requireActivity(), readyToPayTask -> {
            if (readyToPayTask.isSuccessful()) {
                Log.i("AAA", "IsReadyToPay has been successful");
                requestPayment();
            } else {
                Log.w("AAA", "IsReadyToPay failed", readyToPayTask.getException());
            }
        });
    }

    public void requestPayment() {
        Optional<JSONObject> paymentDataRequestJson = GooglePay.getPaymentDataRequest();
        if (!paymentDataRequestJson.isPresent()) {
            Log.i("AAA", "paymentDataRequestJson JSON is not present");
            return;
        }
        PaymentDataRequest request =
            PaymentDataRequest.fromJson(paymentDataRequestJson.get().toString());

        // Since loadPaymentData may show the UI asking the user to select a payment method, we use
        // AutoResolveHelper to wait for the user interacting with it. Once completed,
        // onActivityResult will be called with the result.
        AutoResolveHelper.resolveTask(paymentsClient.loadPaymentData(request), requireActivity(), GOOGLEPAY_REQUEST_CODE);
    }
}