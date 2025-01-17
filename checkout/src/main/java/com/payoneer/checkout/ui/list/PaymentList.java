/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.list;

import com.payoneer.checkout.payment.PaymentInputValues;
import com.payoneer.checkout.ui.model.PaymentCard;
import com.payoneer.checkout.ui.model.PaymentSection;
import com.payoneer.checkout.ui.model.PaymentSession;

import android.app.Activity;
import android.content.Context;
import android.os.IBinder;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

/**
 * The PaymentList showing available payment methods and accounts in a list
 */
public final class PaymentList {
    private final Activity activity;
    private final PaymentListListener listener;
    private final RecyclerView recyclerView;
    private final ListAdapter adapter;
    private final PaymentItemList itemList;

    private PaymentSession session;
    private int nextViewType;

    /**
     * Construct a new PaymentList handling the RecyclerView
     *
     * @param activity that contains this PaymentList
     * @param listener notified about events from this PaymentList
     * @param recyclerView for showing the list of payment options
     */
    public PaymentList(Activity activity, PaymentListListener listener, RecyclerView recyclerView) {
        this.activity = activity;
        this.listener = listener;
        this.recyclerView = recyclerView;

        this.itemList = new PaymentItemList();
        this.adapter = new ListAdapter(createCardListener(), itemList);

        this.recyclerView.setAdapter(adapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();

        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
    }

    public void onStop() {
        hideKeyboard();
    }

    public void clear() {
        if (session != null) {
            session.reset();
            session = null;
        }
        itemList.clear();
        adapter.notifyDataSetChanged();
    }

    public boolean hasUserInputData() {
        return session != null && session.hasUserInputData();
    }

    public void showPaymentSession(PaymentSession session) {
        if (this.session == session) {
            setVisible(true);
            return;
        }
        clear();
        this.session = session;
        setPaymentSessionItems(session);

        setVisible(true);
        adapter.notifyDataSetChanged();

        int scrollPosition = calculateScrollPosition(itemList.getSelectedIndex());
        recyclerView.scrollToPosition(scrollPosition);
    }

    public void setVisible(boolean visible) {
        recyclerView.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    private int calculateScrollPosition(int index) {
        int headerIndex = index - 1;
        ListItem item = itemList.getItem(headerIndex);
        return (HeaderItem.isHeaderItem(item)) ? headerIndex : index;
    }

    private PaymentCardListener createCardListener() {
        return new PaymentCardListener() {
            @Override
            public void onHideKeyboard() {
                hideKeyboard();
            }

            @Override
            public void onShowKeyboard(View view) {
                showKeyboard(view);
            }

            @Override
            public void onDeleteClicked(PaymentCard paymentCard) {
                handleDeleteClicked(paymentCard);
            }

            @Override
            public void onHintClicked(String networkCode, String type) {
                handleHintClicked(networkCode, type);
            }

            @Override
            public void onActionClicked(final PaymentCard paymentCard, final PaymentInputValues inputValues) {
                handleActionClicked(paymentCard, inputValues);
            }

            @Override
            public void onCardClicked(int position) {
                handleCardClicked(position);
            }

            @Override
            public void onShowForcedCheckBoxDialog(final String networkCode) {
                handleShowForcedCheckBoxDialog(networkCode);
            }

            @Override
            public void onExpiredIconClicked(String networkCode) {
                handleExpiredIconClicked(networkCode);
            }
        };
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            View curFocus = activity.getCurrentFocus();
            IBinder binder = curFocus != null ? curFocus.getWindowToken() : recyclerView.getWindowToken();
            imm.hideSoftInputFromWindow(binder, 0);
        }
    }

    private void showKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, 0);
        }
    }

    private void handleDeleteClicked(PaymentCard paymentCard) {
        hideKeyboard();
        listener.onDeleteClicked(paymentCard);
    }

    private void handleHintClicked(String networkCode, String type) {
        listener.onHintClicked(networkCode, type);
    }

    private void handleShowForcedCheckBoxDialog(final String networkCode) {
        listener.onShowForcedMessage(networkCode);
    }

    private void handleExpiredIconClicked(String networkCode) {
        listener.onExpiredIconClicked(networkCode);
    }

    private void handleActionClicked(final PaymentCard paymentCard, final PaymentInputValues inputValues) {
        hideKeyboard();
        listener.onActionClicked(paymentCard, inputValues);
    }

    private void handleCardClicked(int position) {
        int curIndex = itemList.getSelectedIndex();
        if (position == curIndex) {
            itemList.setSelectedIndex(-1);
            adapter.notifyItemChanged(position);
            hideKeyboard();
        } else {
            itemList.setSelectedIndex(position);
            adapter.notifyItemChanged(curIndex);
            adapter.notifyItemChanged(position);
            scrollAndCloseKeyboard(calculateScrollPosition(position));
        }
    }

    private void scrollAndCloseKeyboard(int position) {
        RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(activity) {
            @Override
            protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }

            @Override
            protected void onStop() {
                hideKeyboard();
            }
        };
        smoothScroller.setTargetPosition(position);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();

        if (manager != null) {
            manager.startSmoothScroll(smoothScroller);
        }
    }

    private void setPaymentSessionItems(PaymentSession paymentSession) {
        for (PaymentSection section : paymentSession.getPaymentSections()) {
            addPaymentSectionItems(section);
        }
    }

    private void addPaymentSectionItems(PaymentSection section) {
        itemList.addItem(new HeaderItem(nextViewType(), section.getTitle(), section.getMessage()), false);
        for (PaymentCard card : section.getPaymentCards()) {
            PaymentCardItem item = new PaymentCardItem(nextViewType(), card);
            itemList.addItem(item, card.isPreselected());
        }
    }

    private int nextViewType() {
        return nextViewType++;
    }
}
