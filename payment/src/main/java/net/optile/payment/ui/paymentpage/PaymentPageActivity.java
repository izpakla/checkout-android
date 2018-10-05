/*
 * Copyright(c) 2012-2018 optile GmbH. All Rights Reserved.
 * https://www.optile.net
 * <p>
 * This software is the property of optile GmbH. Distribution  of  this
 * software without agreement in writing is strictly prohibited.
 * <p>
 * This software may not be copied, used or distributed unless agreement
 * has been received in full.
 */

package net.optile.payment.ui.paymentpage;

import java.util.Map;
import android.content.Context;
import android.content.Intent;
import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.support.design.widget.Snackbar;
import net.optile.payment.R;
import net.optile.payment.ui.widget.FormWidget;
import net.optile.payment.ui.PaymentTheme;
import net.optile.payment.ui.PaymentResult;
import net.optile.payment.ui.PaymentUI;
import android.view.MenuItem;

/**
 * The PaymentPageActivity showing available payment methods
 */
public final class PaymentPageActivity extends AppCompatActivity implements PaymentPageView {

    private final static String TAG = "pay_PaymentPageActivity";
    private final static String EXTRA_LISTURL = "extra_listurl";
    private final static String EXTRA_PAYMENTTHEME = "extra_paymenttheme";

    private PaymentPagePresenter presenter;

    private String listUrl;

    private PaymentTheme theme;

    private boolean active;

    private PaymentList paymentList;

    private ProgressBar progressBar;

    private TextView centerMessage;
    
    /**
     * Create the start intent for this Activity
     *
     * @param context Context to create the intent
     * @return newly created start intent
     */
    public static Intent createStartIntent(Context context, String listUrl, PaymentTheme theme) {
        final Intent intent = new Intent(context, PaymentPageActivity.class);
        intent.putExtra(EXTRA_LISTURL, listUrl);
        intent.putExtra(EXTRA_PAYMENTTHEME, theme);
        return intent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(Activity.RESULT_CANCELED, null);

        if (savedInstanceState != null && savedInstanceState.containsKey(EXTRA_LISTURL)) {
            this.listUrl = savedInstanceState.getString(EXTRA_LISTURL);
            this.theme = savedInstanceState.getParcelable(EXTRA_PAYMENTTHEME);
        } else {
            Intent intent = getIntent();
            this.listUrl = intent.getStringExtra(EXTRA_LISTURL);
            this.theme = intent.getParcelableExtra(EXTRA_PAYMENTTHEME);
        }
        setContentView(R.layout.activity_paymentpage);

        this.progressBar = findViewById(R.id.progressbar);
        this.centerMessage = findViewById(R.id.label_center);
        
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        this.presenter = new PaymentPagePresenter(this);
        this.paymentList = new PaymentList(this, findViewById(R.id.recyclerview_paymentlist));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(EXTRA_LISTURL, listUrl);
        savedInstanceState.putParcelable(EXTRA_PAYMENTTHEME, theme);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPause() {
        super.onPause();
        this.active = false;
        this.presenter.onStop();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onResume() {
        super.onResume();
        this.active = true;
        presenter.load(this.listUrl);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
        case android.R.id.home:
            supportFinishAfterTransition();
            return true;
        }
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isActive() {
        return this.active;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Context getContext() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showPaymentSession(PaymentSession session) {

        if (!isActive()) {
            return;
        }
        if (session.getApplicableNetworkSize() == 0) {
            showCenterMessage(R.string.error_paymentpage_empty);
        } else if (session.groups.size() == 0) {
            showCenterMessage(R.string.error_paymentpage_notsupported);
        } else {
            paymentList.showPaymentSession(session);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        if (!isActive()) {
            return;
        }
        centerMessage.setVisibility(View.GONE);
        paymentList.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void closePaymentPage(boolean success, PaymentResult result) {
        if (!isActive()) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(PaymentUI.EXTRA_PAYMENT_RESULT, result);
        setResult(success ? Activity.RESULT_OK : Activity.RESULT_CANCELED, intent);
        finish();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void showLoading(boolean show) {
        if (!isActive()) {
            return;
        }
        if (show) {
            paymentList.setVisible(false);
            centerMessage.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showError(int resId) {
        if (!isActive()) {
            return;
        }
        paymentList.setVisible(true);
        centerMessage.setVisibility(View.VISIBLE);

        String message = getString(resId);
        Snackbar snackbar = Snackbar.make(findViewById(R.id.layout_paymentpage), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }
    
    void makeChargeRequest(PaymentGroup group, Map<String, FormWidget> widgets) {
        presenter.charge(widgets, group);
    }

    private void showCenterMessage(int resId) {
        centerMessage.setText(resId);
        centerMessage.setVisibility(View.VISIBLE);
    }
}
