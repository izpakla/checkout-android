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

import java.util.Optional;

import org.json.JSONException;
import org.json.JSONObject;

import com.braintreepayments.api.BraintreeClient;
import com.braintreepayments.api.GooglePayClient;
import com.braintreepayments.api.GooglePayRequest;
import com.braintreepayments.api.PaymentMethodNonce;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.TransactionInfo;
import com.google.android.gms.wallet.WalletConstants;
import com.payoneer.checkout.R;
import com.payoneer.checkout.localization.Localization;
import com.payoneer.checkout.payment.PaymentRequest;
import com.payoneer.checkout.util.GoogleAdyenUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;

/**
 * The ChargePaymentActivity is the view displaying the loading animation while posting the operation.
 * The presenter of this view will post the PresetAccount operation to the Payment API.
 */
public final class ChargePaymentActivity extends BasePaymentActivity implements BasePaymentView {

    public final static int TYPE_PAYMENT_REQUEST = 1;
    public final static int TYPE_PRESET_ACCOUNT = 2;
    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 991;
    private final static int GOOGLE_REQUEST_CODE = 1000;
    private final static String EXTRA_OPERATION = "operation";
    private final static String EXTRA_PAYMENT_REQUEST = "payment_request";
    private final static String EXTRA_CHARGE_TYPE = "charge_type";
    private final String TAG = ChargePaymentActivity.class.getSimpleName();
    private int chargeType;

    private ChargePaymentPresenter presenter;
    private PaymentRequest paymentRequest;
    // A client for interacting with the Google Pay API.
    private PaymentsClient paymentsClient;

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
     * Get the transition used when this Activity is being started
     */
    public static int getStartTransition() {
        return R.anim.fade_in;
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
        paymentsClient = GoogleAdyenUtils.createPaymentsClient(this);
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

    @Override
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
                Log.i("AAA", "error: " + error);
                // handle error
            }
        });
    }

    @Override
    public void showGooglePayAdyen(String gatewayMerchantId) {
        final Optional<JSONObject> isReadyToPayJson = GoogleAdyenUtils.getIsReadyToPayRequest();
        if (!isReadyToPayJson.isPresent()) {
            Log.i(TAG, "IsReadyToPay JSON is not present");
            return;
        }

        IsReadyToPayRequest request = IsReadyToPayRequest.fromJson(isReadyToPayJson.get().toString());
        Task<Boolean> task = paymentsClient.isReadyToPay(request);
        task.addOnCompleteListener(this, readyToPayTask -> {
            if (readyToPayTask.isSuccessful()) {
                Log.i(TAG, "IsReadyToPay has been successful");
                requestPayment(gatewayMerchantId);
            } else {
                Log.w(TAG, "IsReadyToPay failed", readyToPayTask.getException());
            }
        });
    }

    public void requestPayment(String gatewayMerchantId) {
        Optional<JSONObject> paymentDataRequestJson = GoogleAdyenUtils.getPaymentDataRequest(gatewayMerchantId);
        if (!paymentDataRequestJson.isPresent()) {
            Log.i(TAG, "paymentDataRequestJson JSON is not present");
            return;
        }

        PaymentDataRequest request =
            PaymentDataRequest.fromJson(paymentDataRequestJson.get().toString());

        // Since loadPaymentData may show the UI asking the user to select a payment method, we use
        // AutoResolveHelper to wait for the user interacting with it. Once completed,
        // onActivityResult will be called with the result.
        AutoResolveHelper.resolveTask(
            paymentsClient.loadPaymentData(request),
            this, LOAD_PAYMENT_DATA_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOAD_PAYMENT_DATA_REQUEST_CODE) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    Log.i(TAG, "LOAD_PAYMENT_DATA_REQUEST_CODE has received OK result");
                    PaymentData paymentData = PaymentData.getFromIntent(data);
                    handlePaymentSuccess(paymentData);
                    break;

                case Activity.RESULT_CANCELED:
                    Toast.makeText(this, "You have cancellled the operation!", Toast.LENGTH_SHORT).show();
                    break;

                case AutoResolveHelper.RESULT_ERROR:
                    Log.i(TAG, "LOAD_PAYMENT_DATA_REQUEST_CODE has received an error");
                    Status status = AutoResolveHelper.getStatusFromIntent(data);
                    handleError(status.getStatusCode());
                    break;
            }
        }
    }

    /**
     * PaymentData response object contains the payment information, as well as any additional
     * requested information, such as billing and shipping address.
     *
     * @param paymentData A response object returned by Google after a payer approves payment.
     */
    private void handlePaymentSuccess(PaymentData paymentData) {
        // Token will be null if PaymentDataRequest was not constructed using fromJson(String).
        final String paymentInfo = paymentData.toJson();
        if (paymentInfo == null) {
            return;
        }

        try {
            JSONObject paymentMethodData = new JSONObject(paymentInfo).getJSONObject("paymentMethodData");
            // If the gateway is set to "example", no payment information is returned - instead, the
            // token will only consist of "examplePaymentMethodToken".

            final JSONObject tokenizationData = paymentMethodData.getJSONObject("tokenizationData");
            final String token = tokenizationData.getString("token");
            final JSONObject info = paymentMethodData.getJSONObject("info");
            final String billingName = info.getJSONObject("billingAddress").getString("name");
            Toast.makeText(this, billingName, Toast.LENGTH_LONG).show();

            // Logging token string.
            Log.d(TAG, "Google Pay token is " + token);
            presenter.makeGoogleChargeWithAdyen(token);

        } catch (JSONException e) {
            throw new RuntimeException(
                "ChargePaymentActivity - Error getting the payment data details with the following error " + e.getMessage());
        }
    }

    /**
     * At this stage, the user has already seen a popup informing them an error occurred. Normally,
     * only logging is required.
     *
     * @param statusCode will hold the value of any constant from CommonStatusCode or one of the
     * WalletConstants.ERROR_CODE_* constants.
     * @see <a href="https://developers.google.com/android/reference/com/google/android/gms/wallet/
     * WalletConstants#constant-summary">Wallet Constants Library</a>
     */
    private void handleError(int statusCode) {
        Log.e(TAG, String.format("Error code: %d", statusCode));
        Toast.makeText(this, String.format("Error code: %d", statusCode), Toast.LENGTH_SHORT).show();
    }

    private void sendGoogleNonceToBackend(PaymentMethodNonce nonce) {
        Log.i("AAA", "nonce: " + nonce.getString());
        presenter.makeGoogleCharge(nonce.getString());
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
