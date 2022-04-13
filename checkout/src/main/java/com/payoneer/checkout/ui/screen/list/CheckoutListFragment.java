/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.screen.list;

import static com.payoneer.checkout.localization.LocalizationKey.LIST_TITLE;

import com.payoneer.checkout.R;
import com.payoneer.checkout.localization.Localization;
import com.payoneer.checkout.payment.PaymentInputValues;
import com.payoneer.checkout.ui.dialog.PaymentDialogData;
import com.payoneer.checkout.ui.dialog.PaymentDialogFragment;
import com.payoneer.checkout.ui.dialog.PaymentDialogFragment.PaymentDialogListener;
import com.payoneer.checkout.ui.dialog.PaymentDialogHelper;
import com.payoneer.checkout.ui.list.PaymentList;
import com.payoneer.checkout.ui.list.PaymentListListener;
import com.payoneer.checkout.ui.model.PaymentCard;
import com.payoneer.checkout.ui.model.PaymentSession;
import com.payoneer.checkout.util.ContentEvent;
import com.payoneer.checkout.util.Event;
import com.payoneer.checkout.util.Resource;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * Fragment to show the Payment Session
 */
public class CheckoutListFragment extends Fragment {

    private Toolbar toolbar;
    private CheckoutListViewModel viewModel;
    private PaymentList paymentList;
    private ProgressView progressView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private PaymentDialogHelper dialogHelper;

    public CheckoutListFragment() {
    }

    public static CheckoutListFragment newInstance() {
        CheckoutListFragment fragment = new CheckoutListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_checkout_list, container, false);
        swipeRefreshLayout = view.findViewById(R.id.layout_swiperefresh);
        toolbar = view.findViewById(R.id.toolbar);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initDialogHelper();
        initProgressView(view);
        initSwipeRefreshlayout();
        initToolbar();
        initPaymentList(view);
        initObservers();
    }

    private void initDialogHelper() {
        CheckoutListActivity activity = (CheckoutListActivity) requireActivity();
        dialogHelper = activity.getPaymentDialogHelper();
    }

    private void initProgressView(final View view) {
        progressView = new ProgressView(view.findViewById(R.id.layout_progress));
    }

    private void initSwipeRefreshlayout() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            handleOnSwipeRefresh();
        });
    }

    private void initPaymentList(final View view) {
        PaymentListListener listener = new PaymentListListener() {
            @Override
            public void onActionClicked(final PaymentCard paymentCard, final PaymentInputValues inputValues) {
                viewModel.processPaymentCard(paymentCard, inputValues);
            }

            @Override
            public void onDeleteClicked(final PaymentCard paymentCard) {
                handleOnDeleteClicked(paymentCard);
            }

            @Override
            public void onHintClicked(final String networkCode, final String type) {
                dialogHelper.showHintDialog(getParentFragmentManager(), networkCode, type, null);
            }

            @Override
            public void onExpiredIconClicked(final String networkCode) {
                dialogHelper.showExpiredDialog(getParentFragmentManager(), networkCode, null);
            }
        };
        paymentList = new PaymentList(requireActivity(), listener, view.findViewById(R.id.recyclerview_paymentlist));
    }

    private void initToolbar() {
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        setToolbarTitle("");
        activity.setSupportActionBar(toolbar);

        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    private void setToolbarTitle(final String title) {
        toolbar.setTitle(title);
    }

    private void initObservers() {
        viewModel = new ViewModelProvider(requireActivity()).get(CheckoutListViewModel.class);
        viewModel.showPaymentSession.observe(requireActivity(), new Observer<Resource>() {
            @Override
            public void onChanged(@Nullable Resource resource) {
                if (resource == null) {
                    return;
                }
                switch (resource.getStatus()) {
                    case Resource.SUCCESS:
                        showPaymentSession((PaymentSession) resource.getData());
                        break;
                    case Resource.LOADING:
                        clearPaymentSession();
                        break;
                    case Resource.ERROR:
                        clearPaymentSession();
                        // errors will be shown through the popup dialog observers
                }
            }
        });

        viewModel.showProgressIndicator.observe(requireActivity(), new Observer<Boolean>() {
            @Override
            public void onChanged(final Boolean visible) {
                progressView.setVisible(visible);
            }
        });
    }

    private void clearPaymentSession() {
        paymentList.clear();
        resetSwipeRefreshLayout();
    }

    private void showPaymentSession(final PaymentSession paymentSession) {
        setToolbarTitle(Localization.translate(LIST_TITLE));
        paymentList.showPaymentSession(paymentSession);
        swipeRefreshLayout.setEnabled(paymentSession.swipeRefresh());
    }

    private void handleOnDeleteClicked(final PaymentCard paymentCard) {
        PaymentDialogListener listener = new PaymentDialogListener() {
            @Override
            public void onPositiveButtonClicked() {
                viewModel.deletePaymentCard(paymentCard);
            }

            @Override
            public void onNegativeButtonClicked() {
            }

            @Override
            public void onDismissed() {
            }
        };
        dialogHelper.showConfirmDeleteDialog(getParentFragmentManager(), paymentCard.getTitle(), listener);
    }

    private void handleOnSwipeRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        if (!paymentList.hasUserInputData()) {
            viewModel.loadPaymentSession();
            return;
        }
        PaymentDialogListener listener = new PaymentDialogFragment.PaymentDialogListener() {
            @Override
            public void onPositiveButtonClicked() {
                viewModel.loadPaymentSession();
            }

            @Override
            public void onNegativeButtonClicked() {
            }

            @Override
            public void onDismissed() {
            }
        };
        dialogHelper.showConfirmRefreshDialog(getParentFragmentManager(), listener);
    }

    private void resetSwipeRefreshLayout() {
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);
    }
}