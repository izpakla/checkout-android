/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.page;

import static com.payoneer.checkout.localization.LocalizationKey.CHARGE_TEXT;
import static com.payoneer.checkout.localization.LocalizationKey.CHARGE_TITLE;

import com.payoneer.checkout.CheckoutConfiguration;
import com.payoneer.checkout.R;
import com.payoneer.checkout.localization.Localization;
import com.payoneer.checkout.payment.PaymentRequest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;

/**
 * The ChargePaymentActivity is the view displaying the loading animation while posting the operation.
 * The presenter of this view will post the PresetAccount operation to the Payment API.
 */
public final class ChargePaymentActivity extends BasePaymentActivity implements BasePaymentView {

    private int chargeType;
    private ChargePaymentPresenter presenter;
    private PaymentRequest paymentRequest;
    private CheckoutConfiguration configuration;

    /**
     * Create the start intent for this ChargePaymentActivity
     *
     * @param context Context to create the intent
     * @return newly created start intent
     */
    public static Intent createStartIntent(Context context, CheckoutConfiguration checkoutConfiguration, PaymentRequest paymentRequest) {
        if (context == null) {
            throw new IllegalArgumentException("context may not be null");
        }
        if (paymentRequest == null) {
            throw new IllegalArgumentException("paymentRequest may not be null");
        }
        Intent intent = new Intent(context, ChargePaymentActivity.class);
        intent.putExtra(EXTRA_CHARGE_TYPE, TYPE_CHARGE_OPERATION);
        intent.putExtra(EXTRA_PAYMENT_REQUEST, paymentRequest);
        intent.putExtra(EXTRA_CHECKOUT_CONFIGURATION, checkoutConfiguration);
        return intent;
    }

    /**
     * Create the start intent for this ChargePaymentActivity
     *
     * @param context Context to create the intent
     * @return newly created start intent
     */
    public static Intent createStartIntent(Context context, CheckoutConfiguration checkoutConfiguration) {
        if (context == null) {
            throw new IllegalArgumentException("context may not be null");
        }
        Intent intent = new Intent(context, ChargePaymentActivity.class);
        intent.putExtra(EXTRA_CHARGE_TYPE, TYPE_CHARGE_PRESET_ACCOUNT);
        intent.putExtra(EXTRA_CHECKOUT_CONFIGURATION, checkoutConfiguration);
        return intent;
    }

    /**
     * Get the transition used when this Activity is being started
     *
     * @return the start transition of this activity
     */
    public static int getStartTransition() {
        return R.anim.fade_in;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressLint("WrongConstant")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = savedInstanceState == null ? getIntent().getExtras() : savedInstanceState;
        if (bundle != null) {
            this.paymentRequest = bundle.getParcelable(EXTRA_PAYMENT_REQUEST);
            this.chargeType = bundle.getInt(EXTRA_CHARGE_TYPE);
            this.configuration = bundle.getParcelable(EXTRA_CHECKOUT_CONFIGURATION);
        }
        setRequestedOrientation(configuration.getOrientation());
        int theme = configuration.getCheckoutTheme().getNoToolbarTheme();
        if (theme != 0) {
            setTheme(theme);
        }
        setContentView(R.layout.activity_chargepayment);

        progressView = new ProgressView(findViewById(R.id.layout_progress));
        this.presenter = new ChargePaymentPresenter(configuration, this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelable(EXTRA_PAYMENT_REQUEST, this.paymentRequest);
        savedInstanceState.putParcelable(EXTRA_CHECKOUT_CONFIGURATION, this.configuration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPause() {
        super.onPause();
        presenter.onStop();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onResume() {
        super.onResume();
        presenter.onStart(paymentRequest, chargeType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showProgress(boolean visible) {
        super.showProgress(visible);
        progressView.setLabels(Localization.translate(CHARGE_TITLE), Localization.translate(CHARGE_TEXT));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBackPressed() {
        if (!presenter.onBackPressed()) {
            super.onBackPressed();
            setOverridePendingTransition();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void setOverridePendingTransition() {
        overridePendingTransition(R.anim.no_animation, R.anim.fade_out);
    }
}
