/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.model;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.payoneer.checkout.core.PaymentLinkType;
import com.payoneer.checkout.localization.Localization;
import com.payoneer.checkout.model.AccountMask;
import com.payoneer.checkout.model.ExtraElements;
import com.payoneer.checkout.model.InputElement;
import com.payoneer.checkout.model.PresetAccount;
import com.payoneer.checkout.util.AccountMaskUtils;
import com.payoneer.checkout.util.PaymentUtils;

/**
 * Class for holding the data of a PresetCard in the list
 */
public final class PresetCard extends PaymentCard {
    private final PresetAccount account;
    private final ButtonConfig buttonConfig;

    public PresetCard(PresetAccount account, ButtonConfig buttonConfig, ExtraElements extraElements) {
        super(extraElements);
        this.account = account;
        this.buttonConfig = buttonConfig;
    }

    @Override
    public void putLanguageLinks(Map<String, URL> links) {
        URL url = getLink(PaymentLinkType.LANGUAGE);
        if (url != null) {
            links.put(getNetworkCode(), url);
        }
    }

    @Override
    public String getOperationType() {
        return account.getOperationType();
    }

    @Override
    public boolean isPreselected() {
        return true;
    }

    @Override
    public boolean hasSelectedNetwork() {
        return false;
    }

    @Override
    public String getPaymentMethod() {
        return account.getMethod();
    }

    @Override
    public String getNetworkCode() {
        return account.getCode();
    }

    @Override
    public boolean containsLink(final String name, final URL url) {
        return PaymentUtils.equalsAsString(getLink(name), url);
    }

    @Override
    public ButtonConfig getButtonConfig() {
        return buttonConfig;
    }

    @Override
    public String getTitle() {
        String networkLabel = Localization.translateNetworkLabel(account.getCode());
        AccountMask accountMask = account.getMaskedAccount();
        if (accountMask != null) {
            return AccountMaskUtils.getAccountMaskLabel(accountMask, getPaymentMethod(), networkLabel);
        }
        return networkLabel;
    }

    @Override
    public String getSubtitle() {
        AccountMask accountMask = account.getMaskedAccount();
        return accountMask != null ? AccountMaskUtils.getExpiryDateString(accountMask) : null;
    }

    @Override
    public Map<String, URL> getLinks() {
        return PaymentUtils.emptyMapIfNull(account.getLinks());
    }

    @Override
    public List<InputElement> getInputElements() {
        return Collections.emptyList();
    }

    public PresetAccount getPresetAccount() {
        return account;
    }

    public AccountMask getMaskedAccount() {
        return account.getMaskedAccount();
    }
}
