/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.list;

import com.payoneer.checkout.ui.model.AccountCard;
import com.payoneer.checkout.ui.model.NetworkCard;
import com.payoneer.checkout.ui.model.PaymentCard;
import com.payoneer.checkout.ui.model.PresetCard;

import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

/**
 * The ListAdapter handling the items in this RecyclerView list
 */
final class ListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final PaymentCardListener listener;
    private final PaymentItemList itemList;

    ListAdapter(PaymentCardListener listener, PaymentItemList itemList) {
        this.listener = listener;
        this.itemList = itemList;
    }

    @Override
    public @NonNull
    ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListItem item = itemList.getItemWithViewType(viewType);
        PaymentCard card = item != null ? item.getPaymentCard() : null;

        if (card instanceof NetworkCard) {
            return NetworkCardViewHolder.createInstance(this, (NetworkCard) card, parent);
        } else if (card instanceof AccountCard) {
            return AccountCardViewHolder.createInstance(this, (AccountCard) card, parent);
        } else if (card instanceof PresetCard) {
            return PresetCardViewHolder.createInstance(this, (PresetCard) card, parent);
        } else {
            return HeaderViewHolder.createInstance(parent);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ListItem item = itemList.getItem(position);

        if (item.hasPaymentCard()) {
            PaymentCardViewHolder ph = (PaymentCardViewHolder) holder;
            ph.onBind();
            ph.expand(itemList.getSelectedIndex() == position);
        } else {
            ((HeaderViewHolder) holder).onBind((HeaderItem) item);
        }
    }

    @Override
    public int getItemCount() {
        return itemList.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        return itemList.getItemViewType(position);
    }

    PaymentCardListener getCardListener() {
        return listener;
    }
    
    boolean validPosition(int position) {
        return itemList.validIndex(position);
    }

}
