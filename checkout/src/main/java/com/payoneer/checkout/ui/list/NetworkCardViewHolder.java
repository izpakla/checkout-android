/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.list;


import static com.payoneer.checkout.core.PaymentInputType.ALLOW_RECURRENCE;
import static com.payoneer.checkout.core.PaymentInputType.AUTO_REGISTRATION;

import com.payoneer.checkout.R;
import com.payoneer.checkout.ui.model.NetworkCard;
import com.payoneer.checkout.ui.model.PaymentCard;
import com.payoneer.checkout.ui.model.PaymentNetwork;
import com.payoneer.checkout.ui.model.SmartSwitch;
import com.payoneer.checkout.ui.widget.RegisterWidget;
import com.payoneer.checkout.util.PaymentUtils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

/**
 * The NetworkCardViewHolder
 */
final class NetworkCardViewHolder extends PaymentCardViewHolder {

    private final TextView title;
    private NetworkLogosView networkLogosView;


    public NetworkCardViewHolder(ListAdapter adapter, View parent, NetworkCard networkCard) {
        super(adapter, parent, networkCard);
        this.title = parent.findViewById(R.id.text_title);

        addElementWidgets(networkCard);
        addRegisterWidgets();
        addButtonWidget();
        layoutWidgets();

        if (networkCard.getPaymentNetworkCount() > 1) {
            networkLogosView = new NetworkLogosView(parent, networkCard.getPaymentNetworks());
        }
        setLastImeOptions();
    }

    static ViewHolder createInstance(ListAdapter adapter, NetworkCard networkCard, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_networkcard, parent, false);
        return new NetworkCardViewHolder(adapter, view, networkCard);
    }

    void onBind() {
        super.onBind();

        NetworkCard networkCard = (NetworkCard) paymentCard;
        PaymentNetwork network = networkCard.getVisibleNetwork();
        title.setText(networkCard.getLabel());

        if (networkCard.getPaymentNetworkCount() == 1) {
            bindCardLogo(networkCard.getCode(), networkCard.getLink("logo"));
            setTestId("network");
        } else {
            bindCardLogo(R.drawable.ic_card);
            bindNetworkLogos(networkCard);
            setTestId("group");
        }
        bindRegistrationWidget(network);
        bindRecurrenceWidget(network);
    }

    private void bindNetworkLogos(NetworkCard card) {
        if (networkLogosView == null) {
            return;
        }
        SmartSwitch smartSwitch = card.getSmartSwitch();
        if (smartSwitch.getSelectedCount() == 1) {
            PaymentNetwork network = smartSwitch.getFirstSelected();
            networkLogosView.setSelected(network.getCode());
            return;
        }
        networkLogosView.setSelected(null);
    }

    private void setTestId(String testId) {
        PaymentUtils.setTestId(itemView, "card", testId);
    }

    private void addRegisterWidgets() {
        addWidget(new RegisterWidget(AUTO_REGISTRATION));
        addWidget(new RegisterWidget(ALLOW_RECURRENCE));
    }

    private void bindRegistrationWidget(PaymentNetwork network) {
        RegisterWidget widget = (RegisterWidget) getFormWidget(AUTO_REGISTRATION);
        widget.onBind(network.getRegistration());
    }

    private void bindRecurrenceWidget(PaymentNetwork network) {
        RegisterWidget widget = (RegisterWidget) getFormWidget(ALLOW_RECURRENCE);
        widget.onBind(network.getRecurrence());
    }
}
