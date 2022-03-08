/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.page;

import static com.payoneer.checkout.localization.LocalizationKey.LIST_TITLE;

import com.payoneer.checkout.CheckoutActivityResult;
import com.payoneer.checkout.CheckoutConfiguration;
import com.payoneer.checkout.R;
import com.payoneer.checkout.form.Operation;
import com.payoneer.checkout.localization.Localization;
import com.payoneer.checkout.ui.list.PaymentList;
import com.payoneer.checkout.ui.model.PaymentSession;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * The PaymentListActivity showing available payment methods in a list.
 */
public final class PaymentListActivity extends BasePaymentActivity implements PaymentListView {

    private PaymentListPresenter presenter;
    private PaymentList paymentList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CheckoutConfiguration configuration;

    /**
     * Create the start intent for this PaymentListActivity.
     *
     * @param context Context to create the intent
     * @return newly created start intent
     */
    public static Intent createStartIntent(Context context, CheckoutConfiguration configuration) {
        Intent intent = new Intent(context, PaymentListActivity.class);
        intent.putExtra(EXTRA_CHECKOUT_CONFIGURATION, configuration);
        return intent;
    }

    /**
     * Get the transition used when this Activity is being started
     *
     * @return the start transition of this activity
     */
    public static int getStartTransition() {
        return R.anim.no_animation;
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = savedInstanceState == null ? getIntent().getExtras() : savedInstanceState;
        if (bundle != null) {
            this.configuration = bundle.getParcelable(EXTRA_CHECKOUT_CONFIGURATION);
        }
        setRequestedOrientation(configuration.getOrientation());
        int theme = configuration.getCheckoutTheme().getPaymentListTheme();
        if (theme != 0) {
            setTheme(theme);
        }
        setContentView(R.layout.activity_paymentlist);
        progressView = new ProgressView(findViewById(R.id.layout_progress));
        presenter = new PaymentListPresenter(this, configuration);
        paymentList = new PaymentList(this, presenter, findViewById(R.id.recyclerview_paymentlist));

        initSwipeRefreshlayout();
        initToolbar();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if (this.configuration != null) {
            savedInstanceState.putParcelable(EXTRA_CHECKOUT_CONFIGURATION, this.configuration);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CheckoutActivityResult result = CheckoutActivityResult.fromActivityResult(requestCode, resultCode, data);
        presenter.setCheckoutActivityResult(result);
    }

    @Override
    public void onPause() {
        super.onPause();
        paymentList.onStop();
        presenter.onStop();
        resetSwipeRefreshLayout();
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onStart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            close();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        swipeRefreshLayout.setRefreshing(false);
        overridePendingTransition(R.anim.no_animation, R.anim.no_animation);
    }

    @Override
    public void clearPaymentList() {
        paymentList.clear();
        resetSwipeRefreshLayout();
    }

    @Override
    public void showPaymentSession(PaymentSession session) {
        progressView.setVisible(false);
        setToolbar(Localization.translate(LIST_TITLE));
        paymentList.showPaymentSession(session);
        swipeRefreshLayout.setEnabled(session.swipeRefresh());
        idlingResources.setLoadIdlingState(true);
    }

    @Override
    public void showChargePaymentScreen(int requestCode, Operation operation, CheckoutConfiguration configuration) {
        Intent intent = ChargePaymentActivity.createStartIntent(this, configuration, operation);
        startActivityForResult(intent, requestCode);
        overridePendingTransition(ChargePaymentActivity.getStartTransition(), R.anim.no_animation);
        idlingResources.setCloseIdlingState(true);
    }

    private void initSwipeRefreshlayout() {
        swipeRefreshLayout = findViewById(R.id.layout_swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            presenter.onRefresh(paymentList.hasUserInputData());
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    private void setToolbar(String title) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(title);
    }

    private void resetSwipeRefreshLayout() {
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);
    }
}
