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
import com.payoneer.checkout.ui.model.PaymentNetwork;
import com.payoneer.checkout.ui.model.RegistrationOption;
import com.payoneer.checkout.ui.model.SmartSwitch;
import com.payoneer.checkout.ui.widget.FormWidget;
import com.payoneer.checkout.ui.widget.NetworkLogosWidget;
import com.payoneer.checkout.ui.widget.RegistrationWidget;
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
    private final TextView titleView;

    public NetworkCardViewHolder(ListAdapter adapter, View parent, NetworkCard networkCard) {
        super(adapter, parent, networkCard);
        this.titleView = parent.findViewById(R.id.text_title);

        addExtraElementWidgets(networkCard.getTopExtraElements());
        if (networkCard.getPaymentNetworkCount() > 1) {
            addNetworkLogosWidget();
        }
        addInputElementWidgets(networkCard.getInputElements());
        addRegistrationWidgets();
        addExtraElementWidgets(networkCard.getBottomExtraElements());
        addButtonWidget();

        layoutWidgets();
        setLastImeOptions();
    }

    static ViewHolder createInstance(ListAdapter adapter, NetworkCard networkCard, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_networkcard, parent, false);
        return new NetworkCardViewHolder(adapter, view, networkCard);
    }

    void onBind() {
        NetworkCard networkCard = (NetworkCard) paymentCard;
        bindLabel(titleView, networkCard.getTitle(), false);

        if (networkCard.getPaymentNetworkCount() == 1) {
            bindCardLogo(networkCard.getNetworkCode(), networkCard.getLogoLink());
            setTestId("network");
        } else {
            bindCardLogo(R.drawable.ic_card);
            setTestId("group");
        }
        PaymentNetwork network = networkCard.getVisibleNetwork();
        for (FormWidget widget : widgets.values()) {
            if (widget instanceof RegistrationWidget) {
                bindRegistrationWidget((RegistrationWidget) widget, network);
            } else if (widget instanceof NetworkLogosWidget) {
                bindNetworkLogosWidget((NetworkLogosWidget) widget, networkCard);
            } else {
                bindFormWidget(widget);
            }
        }
    }

    void bindRegistrationWidget(RegistrationWidget widget, PaymentNetwork network) {
        String name = widget.getName();
        RegistrationOption option = name.equals(AUTO_REGISTRATION) ? network.getAutoRegistration() : network.getAllowRecurrence();
        widget.onBind(option);
    }

    private void bindNetworkLogosWidget(NetworkLogosWidget widget, NetworkCard card) {
        widget.onBind(card.getPaymentNetworks());
        SmartSwitch smartSwitch = card.getSmartSwitch();
        if (smartSwitch.getSelectedCount() == 1) {
            PaymentNetwork network = smartSwitch.getFirstSelected();
            widget.setSelected(network.getNetworkCode());
            return;
        }
        widget.setSelected(null);
    }

    private void setTestId(String testId) {
        PaymentUtils.setTestId(itemView, "card", testId);
    }

    private void addNetworkLogosWidget() {
        NetworkLogosWidget widget = new NetworkLogosWidget(NETWORKLOGOS);
        putFormWidget(NETWORKLOGOS, widget);
    }

    private void addRegistrationWidgets() {
        putFormWidget(AUTO_REGISTRATION, new RegistrationWidget(AUTO_REGISTRATION));
        putFormWidget(ALLOW_RECURRENCE, new RegistrationWidget(ALLOW_RECURRENCE));
    }
}
