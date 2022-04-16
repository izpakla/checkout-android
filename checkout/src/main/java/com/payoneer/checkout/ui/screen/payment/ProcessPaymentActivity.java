/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.screen.payment;

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
import com.payoneer.checkout.util.ContentEvent;
import com.payoneer.checkout.util.Event;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

/**
 * The CheckoutPaymentActivity showing available payment methods in a recyclerview and handling
 * payment requests.
 */
public final class ProcessPaymentActivity extends AppCompatActivity {

    final static String EXTRA_CHECKOUT_CONFIGURATION = "checkout_configuration";
    private CheckoutConfiguration configuration;
    private ProcessPaymentViewModel paymentViewModel;
    private PaymentServiceViewModel serviceViewModel;
    private PaymentIdlingResources idlingResources;
    private PaymentDialogHelper dialogHelper;

    /**
     * Create the start intent for this CheckoutPaymentActivity
     *
     * @param context Context to create the intent
     * @return newly created start intent
     */
    public static Intent createStartIntent(final Context context, final CheckoutConfiguration configuration) {
        Intent intent = new Intent(context, ProcessPaymentActivity.class);
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

    public PaymentDialogHelper getPaymentDialogHelper() {
        return dialogHelper;
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
        setContentView(R.layout.activity_fragmentcontainer);
        initViewModels();

        idlingResources = new PaymentIdlingResources(getClass().getSimpleName());
        dialogHelper = new PaymentDialogHelper(idlingResources);
        showProcessPaymentFragment();
    }

    private void initViewModels() {
        ProcessPaymentPresenter presenter = new ProcessPaymentPresenter(configuration);
        ProcessPaymentObserver observer = new ProcessPaymentObserver(presenter);
        getLifecycle().addObserver(observer);

        ViewModelProvider.Factory listFactory = new ProcessPaymentViewModelFactory(getApplicationContext(), presenter);
        paymentViewModel = new ViewModelProvider(this, listFactory).get(ProcessPaymentViewModel.class);

        ViewModelProvider.Factory serviceFactory = new PaymentServiceViewModelFactory(getApplicationContext(), presenter);
        serviceViewModel = new ViewModelProvider(this, serviceFactory).get(PaymentServiceViewModel.class);

        initViewModelObservers();
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
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.no_animation, R.anim.no_animation);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        passActivityResultToFragment(requestCode, resultCode, data);
    }

    private void passActivityResultToFragment(int requestCode, int resultCode, Intent data) {
        FragmentManager fm = getSupportFragmentManager();
        List<Fragment> fragments = fm.getFragments();
        for (Fragment fragment : fragments) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void initViewModelObservers() {
        paymentViewModel.closeWithCheckoutResult.observe(this, new Observer<ContentEvent>() {
            @Override
            public void onChanged(final ContentEvent contentEvent) {
                CheckoutResult checkoutResult = (CheckoutResult) contentEvent.getContentIfNotHandled();
                if (checkoutResult != null) {
                    closeWithCheckoutResult(checkoutResult);
                }
            }
        });

        paymentViewModel.showPaymentDialog.observe(this, new Observer<ContentEvent>() {
            @Override
            public void onChanged(final ContentEvent contentEvent) {
                PaymentDialogData data = (PaymentDialogData) contentEvent.getContentIfNotHandled();
                if (data == null) {
                    return;
                }
                dialogHelper.showPaymentDialog(getSupportFragmentManager(), data);
            }
        });

        serviceViewModel.showFragment.observe(this, new Observer<ContentEvent>() {
            @Override
            public void onChanged(final ContentEvent event) {
                Fragment fragment = (Fragment) event.getContentIfNotHandled();
                if (fragment == null) {
                    return;
                }
                showFragment(fragment);
            }
        });

        paymentViewModel.showProcessPayment.observe(this, new Observer<Event>() {
            @Override
            public void onChanged(final Event event) {
                if (event.getIfNotHandled() != null) {
                    showProcessPaymentFragment();
                }
            }
        });
    }

    private void closeWithCheckoutResult(final CheckoutResult checkoutResult) {
        Intent intent = new Intent();
        CheckoutResultHelper.putIntoResultIntent(checkoutResult, intent);
        setResult(CheckoutActivityResult.getResultCode(checkoutResult), intent);
        close();
    }

    private void showProcessPaymentFragment() {
        createFragmentTransaction()
            .replace(R.id.fragment_container_view, ProcessPaymentFragment.class, null)
            .commitNow();
    }

    private void showFragment(Fragment fragment) {
        createFragmentTransaction()
            .replace(R.id.fragment_container_view, fragment, null)
            .commitNow();
    }

    private FragmentTransaction createFragmentTransaction() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setReorderingAllowed(true);
        return transaction;
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
