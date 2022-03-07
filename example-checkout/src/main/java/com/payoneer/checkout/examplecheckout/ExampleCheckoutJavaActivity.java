/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */
package com.payoneer.checkout.examplecheckout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.test.espresso.IdlingResource;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.payoneer.checkout.Checkout;
import com.payoneer.checkout.CheckoutActivityResult;
import com.payoneer.checkout.CheckoutConfiguration;
import com.payoneer.checkout.CheckoutResult;
import com.payoneer.checkout.CheckoutTheme;
import com.payoneer.checkout.examplecheckout.databinding.ActivityExamplecheckoutBinding;
import com.payoneer.checkout.model.Interaction;
import com.payoneer.checkout.ui.page.idlingresource.SimpleIdlingResource;

/**
 * This is the main Activity of this example app demonstrating how to use the Checkout SDK
 */
public final class ExampleCheckoutJavaActivity extends AppCompatActivity {

    private final static int PAYMENT_REQUEST_CODE = 1;
    private final static int CHARGE_PRESET_ACCOUNT_REQUEST_CODE = 2;
    private ActivityExamplecheckoutBinding binding;
    private CheckoutActivityResult activityResult;
    private SimpleIdlingResource resultHandledIdlingResource;
    private boolean resultHandled;
    private Checkout checkout;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExamplecheckoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonShowPaymentList.setOnClickListener(v -> openPaymentPage());
        binding.buttonChargePresetAcount.setOnClickListener(v -> chargePresetAccount());
    }

    @Override
    public void onResume() {
        super.onResume();
        resultHandled = false;
        if (activityResult != null) {
            showPaymentActivityResult(activityResult);
            setResultHandledIdleState(true);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYMENT_REQUEST_CODE || requestCode == CHARGE_PRESET_ACCOUNT_REQUEST_CODE) {
            activityResult = CheckoutActivityResult.fromActivityResult(requestCode, resultCode, data);
        }
    }

    private void clearPaymentResult() {
        setResultHandledIdleState(false);
        binding.labelResultheader.setVisibility(View.GONE);
        binding.layoutResult.setVisibility(View.GONE);
        this.activityResult = null;
    }

    private void showPaymentActivityResult(CheckoutActivityResult sdkResult) {
        int resultCode = sdkResult.getResultCode();
        binding.labelResultheader.setVisibility(View.VISIBLE);
        binding.layoutResult.setVisibility(View.VISIBLE);
        setText(binding.textResultcode, CheckoutActivityResult.resultCodeToString(resultCode));

        String info = null;
        String code = null;
        String reason = null;
        String error = null;
        CheckoutResult checkoutResult = sdkResult.getPaymentResult();

        if (checkoutResult != null) {
            info = checkoutResult.getResultInfo();
            Interaction interaction = checkoutResult.getInteraction();
            code = interaction.getCode();
            reason = interaction.getReason();
            Throwable cause = checkoutResult.getCause();
            error = cause != null ? cause.getMessage() : null;
        }
        setText(binding.textResultinfo, info);
        setText(binding.textInteractioncode, code);
        setText(binding.textInteractionreason, reason);
        setText(binding.textPaymenterror, error);
    }

    private void setText(TextView textView, String text) {
        if (TextUtils.isEmpty(text)) {
            text = getString(R.string.empty_label);
        }
        textView.setText(text);
    }

    private void showErrorDialog(String message) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle(R.string.dialog_error_title);
        builder.setMessage(message);
        builder.setPositiveButton(getString(R.string.dialog_error_button), null);
        builder.create().show();
    }

    private void openPaymentPage() {
        if (!setListUrl()) {
            return;
        }
        closeKeyboard();
        clearPaymentResult();
        checkout.theme(createPaymentTheme());

        // Uncomment if you like to fix e.g. the orientation to landscape mode
        // checkout.orientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        checkout.showPaymentList(this, PAYMENT_REQUEST_CODE);
    }

    private void chargePresetAccount() {
        if (!setListUrl()) {
            return;
        }
        closeKeyboard();
        clearPaymentResult();
        checkout.chargePresetAccount(this, CHARGE_PRESET_ACCOUNT_REQUEST_CODE);
    }

    private boolean setListUrl() {
        String listUrl = binding.inputListurl.getText().toString().trim();
        if (TextUtils.isEmpty(listUrl) || !Patterns.WEB_URL.matcher(listUrl).matches()) {
            showErrorDialog(getString(R.string.dialog_error_listurl_invalid));
            return false;
        }
        checkout = Checkout.with(listUrl);
        return true;
    }

    private CheckoutTheme createPaymentTheme() {
        if (binding.switchTheme.isChecked()) {
            return CheckoutTheme.createBuilder().
                    setPaymentListTheme(R.style.CustomTheme_Toolbar).
                    setChargePaymentTheme(R.style.CustomTheme_NoToolbar).
                    build();
        } else {
            return CheckoutTheme.createDefault();
        }
    }

    /**
     * Only called from test, creates and returns a new paymentResult handled IdlingResource
     */
    @VisibleForTesting
    public IdlingResource getResultHandledIdlingResource() {
        if (resultHandledIdlingResource == null) {
            resultHandledIdlingResource = new SimpleIdlingResource(getClass().getSimpleName() + "-resultHandledIdlingResource");
        }
        if (resultHandled) {
            resultHandledIdlingResource.setIdleState(true);
        } else {
            resultHandledIdlingResource.reset();
        }
        return resultHandledIdlingResource;
    }

    /**
     * For testing only, set the result handled idle state for the IdlingResource
     */
    private void setResultHandledIdleState(boolean val) {
        resultHandled = val;
        if (resultHandledIdlingResource != null) {
            resultHandledIdlingResource.setIdleState(val);
        }
    }

    private void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            IBinder binder = binding.inputListurl.getWindowToken();
            imm.hideSoftInputFromWindow(binder, 0);
        }
    }
}
