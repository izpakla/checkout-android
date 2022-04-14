/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.page;

import com.payoneer.checkout.CheckoutActivityResult;
import com.payoneer.checkout.CheckoutResult;
import com.payoneer.checkout.CheckoutResultHelper;
import com.payoneer.checkout.R;
import com.payoneer.checkout.localization.InteractionMessage;
import com.payoneer.checkout.localization.Localization;
import com.payoneer.checkout.ui.dialog.PaymentDialogFactory;
import com.payoneer.checkout.ui.dialog.PaymentDialogFragment;
import com.payoneer.checkout.ui.dialog.PaymentDialogFragment.PaymentDialogListener;
import com.payoneer.checkout.ui.page.idlingresource.PaymentIdlingResources;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.app.AppCompatActivity;

/**
 * The base activity for payment activities.
 */
abstract class BasePaymentActivity extends AppCompatActivity implements BasePaymentView {

    final static int TYPE_CHARGE_OPERATION = 1;
    final static int TYPE_CHARGE_PRESET_ACCOUNT = 2;
    final static String EXTRA_PAYMENT_REQUEST = "operation";
    final static String EXTRA_CHARGE_TYPE = "charge_type";
    final static String EXTRA_CHECKOUT_CONFIGURATION = "checkout_configuration";

    ProgressView progressView;

    // Automated testing
    PaymentIdlingResources idlingResources;

    @SuppressLint("WrongConstant")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        idlingResources = new PaymentIdlingResources(getClass().getSimpleName());
    }

    @Override
    public void onResume() {
        super.onResume();
        idlingResources.setCloseIdlingState(false);
    }

    @Override
    public void showProgress(boolean visible) {
        progressView.setVisible(visible);
    }

    @Override
    public void showWarningMessage(String message) {
        if (!TextUtils.isEmpty(message)) {
            PaymentDialogFactory.createSnackbar(getRootView(), message).show();
        }
    }

    @Override
    public void showConnectionErrorDialog(PaymentDialogListener listener) {
        progressView.setVisible(false);
        PaymentDialogFragment dialog = PaymentDialogFactory.createConnectionErrorDialog(listener);
        showPaymentDialog(dialog);
    }

    @Override
    public void showDeleteAccountDialog(PaymentDialogListener listener, String displayLabel) {
        PaymentDialogFragment dialog = PaymentDialogFactory.createConfirmDeleteDialog(listener, displayLabel);
        showPaymentDialog(dialog);
    }

    @Override
    public void showRefreshAccountDialog(PaymentDialogListener listener) {
        PaymentDialogFragment dialog = PaymentDialogFactory.createConfirmRefreshDialog(listener);
        showPaymentDialog(dialog);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showInteractionDialog(InteractionMessage interactionMessage, PaymentDialogListener listener) {
        progressView.setVisible(false);
        PaymentDialogFragment dialog;
        if (Localization.hasInteractionMessage(interactionMessage)) {
            dialog = PaymentDialogFactory.createInteractionDialog(interactionMessage, listener);
        } else {
            dialog = PaymentDialogFactory.createDefaultErrorDialog(listener);
        }
        showPaymentDialog(dialog);
    }

    @Override
    public void showHintDialog(String networkCode, String type, PaymentDialogListener listener) {
        PaymentDialogFragment dialog = PaymentDialogFactory.createHintDialog(networkCode, type, listener);
        showPaymentDialog(dialog);
    }

    @Override
    public void showExpiredDialog(String networkCode) {
        PaymentDialogFragment dialog = PaymentDialogFactory.createExpiredDialog(networkCode, null);
        showPaymentDialog(dialog);
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void setCheckoutResult(int resultCode, CheckoutResult result) {
        setResultIntent(resultCode, result);
    }

    @Override
    public void passOnActivityResult(CheckoutActivityResult checkoutActivityResult) {
        setResultIntent(checkoutActivityResult.getResultCode(), checkoutActivityResult.getCheckoutResult());
        supportFinishAfterTransition();
        idlingResources.setCloseIdlingState(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        idlingResources.setCloseIdlingState(true);
    }

    @Override
    public void close() {
        supportFinishAfterTransition();
        setOverridePendingTransition();
        idlingResources.setCloseIdlingState(true);
    }

    void showPaymentDialog(PaymentDialogFragment dialog) {
        dialog.showDialog(getSupportFragmentManager(), idlingResources);
    }

    /**
     * Get the root view of this Activity.
     *
     * @return the root view
     */
    View getRootView() {
        return ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);
    }

    /**
     * Set the ActivityResult with the given resultCode and CheckoutResult.
     *
     * @param resultCode of the ActivityResult
     * @param result to be added as extra to the intent
     */
    void setResultIntent(int resultCode, CheckoutResult result) {
        Intent intent = new Intent();
        CheckoutResultHelper.putIntoResultIntent(result, intent);
        setResult(resultCode, intent);
    }

    /**
     * Set the overridePendingTransition that will be used when moving back to another Activity
     */
    void setOverridePendingTransition() {
        overridePendingTransition(R.anim.no_animation, R.anim.no_animation);
    }

    /**
     * Only called from UI tests, returns the PaymentIdlingResources instance
     *
     * @return PaymentIdlingResources containing the IdlingResources used in this Activity
     */
    public PaymentIdlingResources getPaymentIdlingResources() {
        return idlingResources;
    }
}
