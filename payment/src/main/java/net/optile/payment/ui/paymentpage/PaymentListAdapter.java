/*
 * Copyright(c) 2012-2018 optile GmbH. All Rights Reserved.
 * https://www.optile.net
 *
 * This software is the property of optile GmbH. Distribution  of  this
 * software without agreement in writing is strictly prohibited.
 *
 * This software may not be copied, used or distributed unless agreement
 * has been received in full.
 */

package net.optile.payment.ui.paymentpage;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.net.URL;
import java.util.List;
import java.util.ArrayList;

import net.optile.payment.R;
import net.optile.payment.model.InputElement;
import net.optile.payment.model.InputElementType;
import net.optile.payment.ui.widget.StringInputWidget;
import net.optile.payment.ui.widget.NumericInputWidget;
import net.optile.payment.ui.widget.IntegerInputWidget;
import net.optile.payment.ui.widget.SelectInputWidget;
import net.optile.payment.ui.widget.CheckBoxInputWidget;
import net.optile.payment.ui.widget.ButtonWidget;

import com.bumptech.glide.Glide;

/**
 * The PaymentListAdapter containing the list of items
 */
class PaymentListAdapter extends RecyclerView.Adapter<PaymentListViewHolder> {

    private final static String BUTTON_WIDGET = "ButtonWidget";
    
    private final List<PaymentGroup> items;

    private OnItemListener listener;

    private PaymentPageActivity activity;
    
    /** 
     * Construct a new PaymentListAdapter
     */
    PaymentListAdapter(final PaymentPageActivity activity) {
        this.activity = activity;
        this.items = new ArrayList<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull PaymentListViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_item_paymentpage, parent, false);

        PaymentGroup group = getGroupWithViewType(viewType);
        PaymentListViewHolder holder = new PaymentListViewHolder(this, view);
        addWidgetsToHolder(holder, group, inflater, parent);
        return holder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBindViewHolder(@NonNull PaymentListViewHolder holder, int position) {
        PaymentGroup group = items.get(position);
        URL logoUrl = group.getLink("logo");
        holder.title.setText(group.getLabel());

        if (logoUrl != null) {
            Glide.with(activity).asBitmap().load(logoUrl.toString()).into(holder.logo);
        }

        String buttonLabel = activity.translate(group.getButton(), null);
        ButtonWidget widget = (ButtonWidget)holder.getFormWidget(BUTTON_WIDGET);

        if (TextUtils.isEmpty(buttonLabel)) {
            widget.setVisible(false);
        } else {
            widget.setLabel(buttonLabel);
            widget.setVisible(true);
        }
    }

    /**
     * Set the item listener in this adapter
     *
     * @param listener the listener interested on events from the item
     */
    public void setListener(final OnItemListener listener) {
        this.listener = listener;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getItemViewType(final int position) {
        return items.get(position).type;
    }
    
    /**
     * Clear all items from this adapter
     */
    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    /**
     * Set new items in this adapter and notify any
     * listeners.
     *
     * @param newItems list of PaymentGroups that should be set
     */
    public void setItems(final List<PaymentGroup> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    void handleOnClick(final int position) {
        if (listener != null) {
            PaymentGroup item = items.get(position);
            listener.onItemClicked(item, position);
        }
    }

    /**
     * Get the PaymentGroup at the given index
     *
     * @param index index of the PaymentGroup
     * @return      PaymentGroup given the index or null if not found
     */
    private PaymentGroup getItemFromIndex(final int index) {
        return index >= 0 && index < items.size() ? items.get(index) : null;
    }

    /**
     * Get the group with its type matching the viewType
     *
     * @param type type of the view
     * @return     PaymentGroup with the same type or null if not found
     */
    private PaymentGroup getGroupWithViewType(final int type) {

        for (PaymentGroup group : items) {
            if (group.type == type) {
                return group;
            }
        }
        return null;
    }

    private void addWidgetsToHolder(final PaymentListViewHolder holder, final PaymentGroup group, final LayoutInflater inflater, ViewGroup parent) {

        for (InputElement element : group.elements) {
            switch (element.getType()) {
            case InputElementType.NUMERIC:
                View view = inflater.inflate(R.layout.widget_input_numeric, parent, false);
                holder.addFormWidget(new NumericInputWidget(element.getName(), view, element));
                break;
            case InputElementType.INTEGER:
                view = inflater.inflate(R.layout.widget_input_integer, parent, false);
                holder.addFormWidget(new IntegerInputWidget(element.getName(), view, element));
                break;
            case InputElementType.SELECT:
                view = inflater.inflate(R.layout.widget_input_select, parent, false);
                holder.addFormWidget(new SelectInputWidget(element.getName(), view, element));
                break;
            case InputElementType.CHECKBOX:
                view = inflater.inflate(R.layout.widget_input_checkbox, parent, false);
                holder.addFormWidget(new CheckBoxInputWidget(element.getName(), view, element));
                break;
            case InputElementType.STRING:
            default:
                view = inflater.inflate(R.layout.widget_input_string, parent, false);
                holder.addFormWidget(new StringInputWidget(element.getName(), view, element));
            }
        }
        View view = inflater.inflate(R.layout.widget_button, parent, false);
        holder.addFormWidget(new ButtonWidget(BUTTON_WIDGET, view));
    }
    
    /**
     * The item listener
     */
    public interface OnItemListener {
        void onItemClicked(PaymentGroup item, int position);
        void onActionClicked(PaymentGroup item, int position);
    }
}
