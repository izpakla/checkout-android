/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.screen.list;

import java.util.Map;

import com.payoneer.checkout.R;
import com.payoneer.checkout.ui.list.PaymentList;
import com.payoneer.checkout.ui.list.PaymentListListener;
import com.payoneer.checkout.ui.model.PaymentCard;
import com.payoneer.checkout.ui.model.PaymentSession;
import com.payoneer.checkout.ui.widget.FormWidget;

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
 *
 */
public class CheckoutListFragment extends Fragment implements PaymentListListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private Toolbar toolbar;
    private CheckoutListViewModel viewModel;
    private PaymentList paymentList;

    public CheckoutListFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CheckoutListFragment.
     */
    public static CheckoutListFragment newInstance() {
        CheckoutListFragment fragment = new CheckoutListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.loadPaymentSession();
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
        viewModel = new ViewModelProvider(requireActivity()).get(CheckoutListViewModel.class);
        initSwipeRefreshlayout();
        initToolbar();
        paymentList = new PaymentList(requireActivity(), this, view.findViewById(R.id.recyclerview_paymentlist));

        viewModel.paymentSession.observe(requireActivity(), new Observer<PaymentSession>() {
            @Override
            public void onChanged(@Nullable PaymentSession paymentSession) {
                if (paymentSession != null) {
                    paymentList.showPaymentSession(paymentSession);
                } else {
                    paymentList.clear();
                }
            }
        });
    }

    private void initSwipeRefreshlayout() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            //presenter.onRefresh(paymentList.hasUserInputData());
            //swipeRefreshLayout.setRefreshing(false);
        });
    }

    private void initToolbar() {
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        toolbar.setTitle("");
        activity.setSupportActionBar(toolbar);

        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    private void setToolbarTitle(String title) {
        toolbar.setTitle(title);
    }

    private void resetSwipeRefreshLayout() {
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);
    }

    @Override
    public void onActionClicked(final PaymentCard paymentCard, final Map<String, FormWidget> widgets) {

    }

    @Override
    public void onDeleteClicked(final PaymentCard paymentCard) {

    }

    @Override
    public void onHintClicked(final String code, final String type) {

    }

    @Override
    public void onExpiredIconClicked(final String networkCode) {

    }
}