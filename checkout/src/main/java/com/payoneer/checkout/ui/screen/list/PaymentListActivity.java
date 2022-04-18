/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.screen.list;

import static com.payoneer.checkout.util.FragmentUtils.hideFragment;
import static com.payoneer.checkout.util.FragmentUtils.removeFragment;
import static com.payoneer.checkout.util.FragmentUtils.showFragment;

import java.util.List;

import com.payoneer.checkout.CheckoutActivityResult;
import com.payoneer.checkout.CheckoutConfiguration;
import com.payoneer.checkout.CheckoutResult;
import com.payoneer.checkout.CheckoutResultHelper;
import com.payoneer.checkout.R;
import com.payoneer.checkout.payment.PaymentServiceViewModel;
import com.payoneer.checkout.payment.PaymentServiceViewModelFactory;
import com.payoneer.checkout.ui.dialog.PaymentDialogData;
import com.payoneer.checkout.ui.dialog.PaymentDialogHelper;
import com.payoneer.checkout.ui.screen.idlingresource.PaymentIdlingResources;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

/**
 * The PaymentListActivity showing available payment methods in a recyclerview and handling
 * payment requests.
 */
public final class PaymentListActivity extends AppCompatActivity {

    private final static String FRAGMENT_TRANSACTION = "fragment_transaction";
    private final static String FRAGMENT_PAYMENTLIST = "fragment_paymentlist";
    private final static String FRAGMENT_CUSTOM = "fragment_custom";
    private final static String EXTRA_CHECKOUT_CONFIGURATION = "checkout_configuration";

    private CheckoutConfiguration configuration;
    private PaymentListViewModel listViewModel;
    private PaymentServiceViewModel serviceViewModel;
    private PaymentIdlingResources idlingResources;
    private PaymentDialogHelper dialogHelper;

    public static Intent createStartIntent(final Context context, final CheckoutConfiguration configuration) {
        Intent intent = new Intent(context, PaymentListActivity.class);
        intent.putExtra(EXTRA_CHECKOUT_CONFIGURATION, configuration);
        return intent;
    }

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
        int theme = configuration.getCheckoutTheme().getToolbarTheme();
        if (theme != 0) {
            setTheme(theme);
        }
        setContentView(R.layout.activity_fragment_container);
        initViewModels();
        initObservers();

        idlingResources = new PaymentIdlingResources(getClass().getSimpleName());
        dialogHelper = new PaymentDialogHelper(idlingResources);

        showPaymentListFragment();
    }

    private void initViewModels() {
        PaymentListPresenter presenter = new PaymentListPresenter(configuration);
        PaymentListObserver observer = new PaymentListObserver(presenter);
        getLifecycle().addObserver(observer);

        ViewModelProvider.Factory listFactory = new PaymentListViewModelFactory(getApplicationContext(), presenter);
        listViewModel = new ViewModelProvider(this, listFactory).get(PaymentListViewModel.class);

        ViewModelProvider.Factory serviceFactory = new PaymentServiceViewModelFactory(getApplicationContext(), presenter);
        serviceViewModel = new ViewModelProvider(this, serviceFactory).get(PaymentServiceViewModel.class);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if (this.configuration != null) {
            savedInstanceState.putParcelable(EXTRA_CHECKOUT_CONFIGURATION, this.configuration);
        }
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
        overridePendingTransition(R.anim.no_animation, R.anim.no_animation);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        passActivityResultToFragment(requestCode, resultCode, data);
    }

    public PaymentIdlingResources getPaymentIdlingResources() {
        return idlingResources;
    }

    private void passActivityResultToFragment(int requestCode, int resultCode, Intent data) {
        FragmentManager fm = getSupportFragmentManager();
        List<Fragment> fragments = fm.getFragments();
        for (Fragment fragment : fragments) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void initObservers() {
        listViewModel.closeWithCheckoutResult.observe(this, contentEvent -> {
            CheckoutResult checkoutResult = contentEvent.getContentIfNotHandled();
            if (checkoutResult != null) {
                closeWithCheckoutResult(checkoutResult);
            }
        });

        listViewModel.showPaymentDialog.observe(this, contentEvent -> {
            PaymentDialogData data = contentEvent.getContentIfNotHandled();
            if (data != null) {
                dialogHelper.showPaymentDialog(getSupportFragmentManager(), data);
            }
        });

        serviceViewModel.showCustomFragment.observe(this, contentEvent -> {
            Fragment fragment = contentEvent.getContentIfNotHandled();
            if (fragment != null) {
                FragmentManager manager = getSupportFragmentManager();
                hideFragment(manager, FRAGMENT_PAYMENTLIST);
                hideFragment(manager, FRAGMENT_TRANSACTION);
                showFragment(manager, R.id.fragment_container_view, fragment, FRAGMENT_CUSTOM);
            }
        });

        listViewModel.showPaymentListFragment.observe(this, event -> {
            if (event.getIfNotHandled() != null) {
                FragmentManager manager = getSupportFragmentManager();
                removeFragment(manager, FRAGMENT_CUSTOM);
                hideFragment(manager, FRAGMENT_TRANSACTION);
                showPaymentListFragment();
            }
        });

        listViewModel.showTransactionFragment.observe(this, event -> {
            if (event.getIfNotHandled() != null) {
                FragmentManager manager = getSupportFragmentManager();
                removeFragment(manager, FRAGMENT_CUSTOM);
                hideFragment(manager, FRAGMENT_PAYMENTLIST);
                showFragment(manager, R.id.fragment_container_view, TransactionFragment.class, FRAGMENT_TRANSACTION);
            }
        });
    }

    private void showPaymentListFragment() {
        FragmentManager manager = getSupportFragmentManager();
        showFragment(manager, R.id.fragment_container_view, PaymentListFragment.class, FRAGMENT_PAYMENTLIST);
    }

    private void closeWithCheckoutResult(final CheckoutResult checkoutResult) {
        Intent intent = new Intent();
        CheckoutResultHelper.putIntoResultIntent(checkoutResult, intent);
        setResult(CheckoutActivityResult.getResultCode(checkoutResult), intent);
        close();
    }

    private void close() {
        supportFinishAfterTransition();
        setOverridePendingTransition();
        idlingResources.setCloseIdlingState(true);
    }

    private void setOverridePendingTransition() {
        overridePendingTransition(R.anim.no_animation, R.anim.no_animation);
    }
}
