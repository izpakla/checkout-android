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

import com.braintreepayments.api.BraintreeClient;
import com.braintreepayments.api.GooglePayClient;
import com.braintreepayments.api.GooglePayRequest;
import com.braintreepayments.api.PaymentMethodNonce;
import com.google.android.gms.wallet.TransactionInfo;
import com.google.android.gms.wallet.WalletConstants;
import com.payoneer.checkout.R;
import com.payoneer.checkout.localization.Localization;
import com.payoneer.checkout.payment.PaymentRequest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;

/**
 * The ChargePaymentActivity is the view displaying the loading animation while posting the operation.
 * The presenter of this view will post the PresetAccount operation to the Payment API.
 */
public final class ChargePaymentActivity extends BasePaymentActivity implements BasePaymentView {

    private final static int GOOGLE_REQUEST_CODE = 1000;
    private final static String EXTRA_OPERATION = "operation";
    private final static String EXTRA_PAYMENT_REQUEST = "payment_request";
    private final static String EXTRA_CHARGE_TYPE = "charge_type";
    public final static int TYPE_PAYMENT_REQUEST = 1;
    public final static int TYPE_PRESET_ACCOUNT = 2;
    private int chargeType;
    private ChargePaymentPresenter presenter;
    private PaymentRequest paymentRequest;

    private BraintreeClient braintreeClient;
    private GooglePayClient googlePayClient;

    /**
     * Create the start intent for this ChargePaymentActivity
     *
     * @param context Context to create the intent
     * @return newly created start intent
     */
    public static Intent createStartIntent(Context context, PaymentRequest paymentRequest) {
        if (context == null) {
            throw new IllegalArgumentException("context may not be null");
        }
        if (paymentRequest == null) {
            throw new IllegalArgumentException("operation may not be null");
        }
        Intent intent = new Intent(context, ChargePaymentActivity.class);
        intent.putExtra(EXTRA_CHARGE_TYPE, TYPE_PAYMENT_REQUEST);
        intent.putExtra(EXTRA_PAYMENT_REQUEST, paymentRequest);
        return intent;
    }

    /**
     * Create the start intent for this ChargePaymentActivity
     *
     * @param context Context to create the intent
     * @return newly created start intent
     */
    public static Intent createStartIntent(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context may not be null");
        }
        Intent intent = new Intent(context, ChargePaymentActivity.class);
        intent.putExtra(EXTRA_CHARGE_TYPE, TYPE_PRESET_ACCOUNT);
        return intent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int theme = getPaymentTheme().getChargePaymentTheme();
        if (theme != 0) {
            setTheme(theme);
        }
        Bundle bundle = savedInstanceState == null ? getIntent().getExtras() : savedInstanceState;
        if (bundle != null) {
            this.paymentRequest = bundle.getParcelable(EXTRA_PAYMENT_REQUEST);
            this.chargeType = bundle.getInt(EXTRA_CHARGE_TYPE);
        }
        setContentView(R.layout.activity_chargepayment);

        progressView = new ProgressView(findViewById(R.id.layout_progress));
        this.presenter = new ChargePaymentPresenter(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if (this.paymentRequest != null) {
            savedInstanceState.putParcelable(EXTRA_PAYMENT_REQUEST, this.paymentRequest);
        }
    }


    public void showGooglePay(final String auth) {
        Log.i("AAA", "auth: " + auth);
        braintreeClient = new BraintreeClient(this, auth);
        googlePayClient = new GooglePayClient(braintreeClient);

        GooglePayRequest googlePayRequest = new GooglePayRequest();
        googlePayRequest.setTransactionInfo(TransactionInfo.newBuilder()
            .setTotalPrice("1.00")
            .setTotalPriceStatus(WalletConstants.TOTAL_PRICE_STATUS_FINAL)
            .setCurrencyCode("EUR")
            .build());
        googlePayRequest.setBillingAddressRequired(true);

        googlePayClient.requestPayment(this, googlePayRequest, error -> {
            if (error != null) {
                Log.i("AAA", "error: "+ error);
                // handle error
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("AAA", "requestCode: " + requestCode + ", " + resultCode + ", " + data);
        //if (requestCode == GOOGLE_REQUEST_CODE) {
            googlePayClient.onActivityResult(resultCode, data, (paymentMethodNonce, error) -> {
                sendGoogleNonceToBackend(paymentMethodNonce);
            });
        //}
    }

    private void sendGoogleNonceToBackend(PaymentMethodNonce nonce) {
        Log.i("AAA", "nonce: " + nonce.getString());
        presenter.makeGoogleCharge(nonce.getString());
    }

    /**
     * Get the transition used when this Activity is being started
     */
    public static int getStartTransition() {
        return R.anim.fade_in;
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
