/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.session;

import static com.payoneer.checkout.model.IntegrationType.MOBILE_NATIVE;
import static com.payoneer.checkout.model.NetworkOperationType.CHARGE;
import static com.payoneer.checkout.model.NetworkOperationType.PRESET;
import static com.payoneer.checkout.model.NetworkOperationType.UPDATE;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.payoneer.checkout.CheckoutConfiguration;
import com.payoneer.checkout.R;
import com.payoneer.checkout.core.PaymentException;
import com.payoneer.checkout.core.WorkerSubscriber;
import com.payoneer.checkout.core.WorkerTask;
import com.payoneer.checkout.core.Workers;
import com.payoneer.checkout.localization.LocalLocalizationHolder;
import com.payoneer.checkout.localization.Localization;
import com.payoneer.checkout.localization.LocalizationCache;
import com.payoneer.checkout.localization.LocalizationHolder;
import com.payoneer.checkout.localization.MultiLocalizationHolder;
import com.payoneer.checkout.model.ListResult;
import com.payoneer.checkout.network.ListConnection;
import com.payoneer.checkout.network.LocalizationConnection;
import com.payoneer.checkout.resource.PaymentGroup;
import com.payoneer.checkout.resource.ResourceLoader;
import com.payoneer.checkout.risk.RiskProviders;
import com.payoneer.checkout.ui.model.PaymentSession;
import com.payoneer.checkout.validation.Validator;

import android.content.Context;
import android.util.Log;

/**
 * The PaymentSessionService providing asynchronous loading of the PaymentSession, validator and localizations.
 * This service makes callbacks in the listener to notify of request completions.
 */
public final class PaymentSessionService {

    /**
     * Memory cache of localizations
     */
    private static final LocalizationCache cache = new LocalizationCache();
    private final ListConnection listConnection;
    private final LocalizationConnection locConnection;
    private PaymentSessionListener listener;
    private WorkerTask<PaymentSession> sessionTask;

    /**
     * Create a new PaymentSessionService, this service is used to load the PaymentSession.
     */
    public PaymentSessionService() {
        this.listConnection = new ListConnection();
        this.locConnection = new LocalizationConnection();
    }

    /**
     * Set the session listener which will be informed about the state of a payment session being loaded.
     *
     * @param listener to be informed about the payment session being loaded
     */
    public void setListener(PaymentSessionListener listener) {
        this.listener = listener;
    }

    /**
     * Notify that the processing should be stopped
     */
    public void stop() {
        WorkerTask.unsubscribe(sessionTask);
        sessionTask = null;
    }

    /**
     * Check if this service is currently active, i.e. is is loading a payment session or posting an operation.
     *
     * @return true when active, false otherwise
     */
    public boolean isActive() {
        return WorkerTask.isSubscribed(sessionTask);
    }

    /**
     * Load the PaymentSession with the given listUrl, this will load the list result, languages and validator.
     *
     * @param configuration is the object containing URL pointing to the list on the Payment API
     * @param context Android context in which this service is used
     */
    public void loadPaymentSession(final CheckoutConfiguration configuration, final Context context) {

        if (sessionTask != null) {
            throw new IllegalStateException("Already loading payment session, stop first");
        }
        sessionTask = WorkerTask.fromCallable(() -> asyncLoadPaymentSession(configuration.getListURL(), context));
        sessionTask.subscribe(new WorkerSubscriber<PaymentSession>() {
            @Override
            public void onSuccess(PaymentSession paymentSession) {
                sessionTask = null;

                if (listener != null) {
                    listener.onPaymentSessionSuccess(paymentSession);
                }
            }

            @Override
            public void onError(Throwable cause) {
                Log.w("checkout-sdk", cause);
                sessionTask = null;

                if (listener != null) {
                    listener.onPaymentSessionError(cause);
                }
            }
        });
        Workers.getInstance().forNetworkTasks().execute(sessionTask);
    }

    /**
     * Check if the provided operationType is supported by this PaymentSessionService
     *
     * @param operationType the operation type to check
     * @return true when supported, false otherwise
     */
    public boolean isSupportedNetworkOperationType(String operationType) {
        if (operationType == null) {
            return false;
        }
        switch (operationType) {
            case CHARGE:
            case PRESET:
            case UPDATE:
                return true;
            default:
                return false;
        }
    }

    private PaymentSession asyncLoadPaymentSession(URL listURL, Context context) throws PaymentException {
        listConnection.initialize(context);
        locConnection.initialize(context);

        ListResult listResult = listConnection.getListResult(listURL);

        String integrationType = listResult.getIntegrationType();
        if (!MOBILE_NATIVE.equals(integrationType)) {
            throw new PaymentException("Integration type is not supported: " + integrationType);
        }
        String operationType = listResult.getOperationType();
        if (!isSupportedNetworkOperationType(operationType)) {
            throw new PaymentException("List operationType is not supported: " + operationType);
        }
        PaymentSession session = new PaymentSessionBuilder()
            .setListResult(listResult)
            .setPaymentGroups(loadPaymentGroups(context))
            .build();

        loadValidator(context);
        loadLocalizations(session, context);
        loadRiskProviders(session, context);
        return session;
    }

    private Map<String, PaymentGroup> loadPaymentGroups(Context context) throws PaymentException {
        return ResourceLoader.loadPaymentGroups(context.getResources(), R.raw.groups);
    }

    private void loadValidator(Context context) throws PaymentException {
        if (Validator.getInstance() == null) {
            Validator validator = new Validator(ResourceLoader.loadValidations(context.getResources(), R.raw.validations));
            Validator.setInstance(validator);
        }
    }

    private void loadLocalizations(final PaymentSession session, final Context context) throws PaymentException {
        LocalizationHolder localHolder = new LocalLocalizationHolder(context);
        LocalizationHolder sharedHolder = loadLocalizationHolder(session.getListLanguageLink(), localHolder);

        Map<String, LocalizationHolder> holders = new HashMap<>();
        Map<String, URL> links = session.getLanguageLinks();
        for (Map.Entry<String, URL> entry : links.entrySet()) {
            holders.put(entry.getKey(), loadLocalizationHolder(entry.getValue(), sharedHolder));
        }
        Localization.setInstance(new Localization(sharedHolder, holders));
    }

    private LocalizationHolder loadLocalizationHolder(URL url, LocalizationHolder fallback) throws PaymentException {
        String langUrl = url.toString();
        LocalizationHolder holder = cache.get(langUrl);

        if (holder == null) {
            holder = new MultiLocalizationHolder(locConnection.loadLocalization(url), fallback);
            cache.put(langUrl, holder);
        }
        return holder;
    }

    private void loadRiskProviders(final PaymentSession session, final Context context) {
        String listUrl = session.getListSelfUrl();
        RiskProviders riskProviders = RiskProviders.getInstance();

        if (riskProviders != null && riskProviders.containsRiskProvidersId(listUrl)) {
            return;
        }
        riskProviders = new RiskProviders(listUrl);
        riskProviders.initializeRiskProviders(session.getRiskProviders(), context);
        RiskProviders.setInstance(riskProviders);
    }
}
