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

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import android.support.v7.widget.RecyclerView;
import net.optile.payment.R;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Animation;
import android.view.animation.AccelerateInterpolator;
import net.optile.payment.ui.widget.FormWidget;
import net.optile.payment.ui.widget.ButtonWidget;

import java.util.LinkedHashMap;

/**
 * The PaymentListViewHolder holding all Views for easy access
 */
class PaymentListViewHolder extends RecyclerView.ViewHolder {

    final TextView title;

    final ImageView logo;

    final ViewGroup formLayout;
    
    final PaymentListAdapter adapter;

    private LinkedHashMap<String, FormWidget> widgets;

    private int viewType;
    
    PaymentListViewHolder(PaymentListAdapter adapter, View parent, int viewType) {
        super(parent);
        this.viewType = viewType;
        this.adapter = adapter;
        this.title = parent.findViewById(R.id.text_title);
        this.logo = parent.findViewById(R.id.image_logo);
        this.formLayout = parent.findViewById(R.id.layout_form);
        this.widgets = new LinkedHashMap<>();
        
        View view = parent.findViewById(R.id.layout_header);
        view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.handleOnClick(getAdapterPosition());
                }
            });
    }

    void expand(boolean expand) {
        if (expand) {
            formLayout.setVisibility(View.VISIBLE);
        } else {
            formLayout.setVisibility(View.GONE);
        }
    }

    void addFormWidget(FormWidget widget) {
        widgets.put(widget.getName(), widget);
        formLayout.addView(widget.getRootView());
    }

    FormWidget getFormWidget(String name) {
        return widgets.get(name);
    }
}
