/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.screen.payment;

import static com.payoneer.checkout.util.FragmentUtils.hideFragment;
import static com.payoneer.checkout.util.FragmentUtils.removeFragment;
import static com.payoneer.checkout.util.FragmentUtils.showFragment;

import java.util.List;

import com.payoneer.checkout.CheckoutActivityResult;
import com.payoneer.checkout.CheckoutConfiguration;
import com.payoneer.checkout.CheckoutResult;
import com.payoneer.checkout.CheckoutResultHelper;
import com.payoneer.checkout.R;
import com.payoneer.checkout.payment.PaymentServiceInteractor;
import com.payoneer.checkout.payment.PaymentServiceViewModel;
import com.payoneer.checkout.payment.PaymentServiceViewModelFactory;
import com.payoneer.checkout.ui.dialog.PaymentDialogData;
import com.payoneer.checkout.ui.dialog.PaymentDialogHelper;
import com.payoneer.checkout.ui.screen.idlingresource.PaymentIdlingResources;
import com.payoneer.checkout.ui.session.PaymentSessionInteractor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

/**
 * The ProcessPaymentActivity is used for charging the PresetAccount.
 */
public final class ProcessPaymentActivity extends AppCompatActivity {

    private final static String FRAGMENT_CUSTOM = "fragment_custom";
    private final static String FRAGMENT_PAYMENT = "fragment_payment";
    private final static String EXTRA_CHECKOUT_CONFIGURATION = "checkout_configuration";

    private CheckoutConfiguration configuration;
    private PaymentIdlingResources idlingResources;
    private PaymentDialogHelper dialogHelper;

    public static Intent createStartIntent(final Context context, final CheckoutConfiguration configuration) {
        Intent intent = new Intent(context, ProcessPaymentActivity.class);
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

        idlingResources = new PaymentIdlingResources(getClass().getSimpleName());
        dialogHelper = new PaymentDialogHelper(idlingResources);
        showProcessPaymentFragment();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if (this.configuration != null) {
            savedInstanceState.putParcelable(EXTRA_CHECKOUT_CONFIGURATION, this.configuration);
        }
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

    private void initViewModels() {

        PaymentSessionInteractor sessionInteractor = new PaymentSessionInteractor(configuration);
        PaymentServiceInteractor serviceInteractor = new PaymentServiceInteractor();

        ViewModelProvider.Factory paymentFactory = new ProcessPaymentViewModelFactory(getApplicationContext(), sessionInteractor, serviceInteractor);
        ProcessPaymentViewModel paymentViewModel = new ViewModelProvider(this, paymentFactory).get(ProcessPaymentViewModel.class);

        ProcessPaymentLifecycleObserver observer = new ProcessPaymentLifecycleObserver(paymentViewModel);
        getLifecycle().addObserver(observer);

        ViewModelProvider.Factory serviceFactory = new PaymentServiceViewModelFactory(getApplicationContext(), serviceInteractor);
        new ViewModelProvider(this, serviceFactory).get(PaymentServiceViewModel.class);

        initObservers(paymentViewModel);
    }

    private void initObservers(final ProcessPaymentViewModel paymentViewModel) {
        paymentViewModel.closeWithCheckoutResult().observe(this, contentEvent -> {
            CheckoutResult checkoutResult = contentEvent.getContentIfNotHandled();
            if (checkoutResult != null) {
                closeWithCheckoutResult(checkoutResult);
            }
        });

        paymentViewModel.showPaymentDialog().observe(this, contentEvent -> {
            PaymentDialogData data = contentEvent.getContentIfNotHandled();
            if (data != null) {
                dialogHelper.showPaymentDialog(getSupportFragmentManager(), data);
            }
        });

        paymentViewModel.showProcessPaymentFragment().observe(this, event -> {
            if (event.getIfNotHandled() != null) {
                removeFragment(getSupportFragmentManager(), FRAGMENT_CUSTOM);
                showProcessPaymentFragment();
            }
        });

        paymentViewModel.showCustomFragment().observe(this, contentEvent -> {
            Fragment customFragment = contentEvent.getContentIfNotHandled();
            if (customFragment != null) {
                FragmentManager manager = getSupportFragmentManager();
                hideFragment(manager, FRAGMENT_PAYMENT);
                showFragment(manager, R.id.fragment_container_view, customFragment, FRAGMENT_CUSTOM);
            }
        });
    }

    private void showProcessPaymentFragment() {
        FragmentManager manager = getSupportFragmentManager();
        showFragment(manager, R.id.fragment_container_view, ProcessPaymentFragment.class, FRAGMENT_PAYMENT);
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
