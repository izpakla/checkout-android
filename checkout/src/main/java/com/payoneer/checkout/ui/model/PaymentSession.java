/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.model;

import static com.payoneer.checkout.core.PaymentLinkType.LANGUAGE;
import static com.payoneer.checkout.core.PaymentLinkType.OPERATION;
import static com.payoneer.checkout.core.PaymentLinkType.SELF;

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.payoneer.checkout.model.ListResult;
import com.payoneer.checkout.model.ProviderParameters;

/**
 * Class for storing the ListResult and the payment sections. The following sections
 * are supported: preset accounts, saved accounts and payment networks.
 */
public final class PaymentSession {

    private final ListResult listResult;
    private final List<PaymentSection> paymentSections;
    private final boolean swipeRefresh;

    /**
     * Construct a new PaymentSession object
     *
     * @param listResult Object holding the current list session data
     * @param paymentSections the list of sections containing PaymentCards
     * @param swipeRefresh can this PaymentSession be refreshed by the user
     */
    public PaymentSession(ListResult listResult, List<PaymentSection> paymentSections, boolean swipeRefresh) {
        this.listResult = listResult;
        this.paymentSections = paymentSections;
        this.swipeRefresh = swipeRefresh;
    }

    public ListResult getListResult() {
        return listResult;
    }

    public List<PaymentSection> getPaymentSections() {
        return paymentSections;
    }

    public boolean swipeRefresh() {
        return swipeRefresh;
    }

    public URL getListLanguageLink() {
        return getListLink(LANGUAGE);
    }

    public String getListSelfUrl() {
        URL url = getListLink(SELF);
        return url != null ? url.toString() : null;
    }

    public String getListOperationType() {
        return listResult.getOperationType();
    }

    public URL getListLink(String name) {
        Map<String, URL> links = listResult.getLinks();
        return links != null ? links.get(name) : null;
    }

    public void reset() {
        for (PaymentSection section : paymentSections) {
            section.reset();
        }
    }

    public boolean hasUserInputData() {
        for (PaymentSection section : paymentSections) {
            if (section.hasUserInputData()) {
                return true;
            }
        }
        return false;
    }

    public boolean isEmpty() {
        return paymentSections.size() == 0;
    }

    public boolean containsOperationLink(URL url) {
        for (PaymentSection section : paymentSections) {
            if (section.containsLink(OPERATION, url)) {
                return true;
            }
        }
        return false;
    }

    public Map<String, URL> getLanguageLinks() {
        Map<String, URL> links = new HashMap<>();
        for (PaymentSection section : paymentSections) {
            section.putLanguageLinks(links);
        }
        return links;
    }

    public List<ProviderParameters> getRiskProviders() {
        List<ProviderParameters> providers = listResult.getRiskProviders();
        return providers != null ? providers : Collections.emptyList();
    }
}