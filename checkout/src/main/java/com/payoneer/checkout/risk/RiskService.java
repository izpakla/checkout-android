/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.risk;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import com.payoneer.checkout.core.PaymentException;
import com.payoneer.checkout.core.WorkerSubscriber;
import com.payoneer.checkout.core.WorkerTask;
import com.payoneer.checkout.core.Workers;
import com.payoneer.checkout.model.ListResult;
import com.payoneer.checkout.model.ProviderParameters;

import android.content.Context;
import android.util.Log;

/**
 * The RiskService providing asynchronous initialization of risk providers and collection of risk data from these external risk services.
 * This service makes callbacks in the risk listener to notify of request completions.
 */
public final class RiskService {
    private final List<RiskProviderController> controllers;
    private WorkerTask<Void> initTask;
    private WorkerTask<List<ProviderParameters>> collectTask;
    private RiskListener listener;

    public RiskService() {
        controllers = new ArrayList<>();
    }

    /**
     * Set the risk listener which will be informed about the state of the requests
     *
     * @param listener to be informed about the risk calls
     */
    public void setListener(RiskListener listener) {
        this.listener = listener;
    }

    /**
     * Stop and unsubscribe from the task that is currently active in this service.
     */
    public void stop() {
        WorkerTask.unsubscribe(initTask);
        initTask = null;

        WorkerTask.unsubscribe(collectTask);
        collectTask = null;
    }

    /**
     * Check if this service is currently active e.g. collecting risk data
     *
     * @return true when active, false otherwise
     */
    public boolean isActive() {
        return WorkerTask.isSubscribed(initTask) || WorkerTask.isSubscribed(collectTask);
    }

    /**
     * Initialize risk providers provided through in listResult
     *
     * @param context for setting up the risk providers
     * @param listResult containing the risk provider parameters
     */
    public void initializeRisk(Context context, ListResult listResult) {

        if (isActive()) {
            throw new IllegalStateException("RiskService is already active, stop first");
        }
        initTask = WorkerTask.fromCallable(new Callable<Void>() {
            @Override
            public Void call() throws PaymentException {
                return asyncInitializeRisk(context, listResult);
            }
        });
        initTask.subscribe(new WorkerSubscriber<Void>() {
            @Override
            public void onSuccess(Void value) {
                initTask = null;
                if (listener != null) {
                    listener.onRiskInitializedSuccess();
                }
            }

            @Override
            public void onError(Throwable cause) {
                initTask = null;
                if (listener != null) {
                    listener.onRiskInitializedError(cause);
                }
            }
        });
        Workers.getInstance().forNetworkTasks().execute(initTask);
    }

    /**
     * Collect the result from all risk providers initialized in this service
     */
    public void collectRiskData() {

        if (isActive()) {
            throw new IllegalStateException("RiskService is already active, stop first");
        }
        collectTask = WorkerTask.fromCallable(new Callable<List<ProviderParameters>>() {
            @Override
            public List<ProviderParameters> call() throws PaymentException {
                return asyncCollectRiskData();
            }
        });
        collectTask.subscribe(new WorkerSubscriber<List<ProviderParameters>>() {
            @Override
            public void onSuccess(List<ProviderParameters> riskResult) {
                collectTask = null;

                if (listener != null) {
                    listener.onRiskCollectionSuccess(riskResult);
                }
            }

            @Override
            public void onError(Throwable cause) {
                collectTask = null;

                if (listener != null) {
                    listener.onRiskCollectionError(cause);
                }
            }
        });
        Workers.getInstance().forNetworkTasks().execute(collectTask);
    }

    private Void asyncInitializeRisk(final Context context, final ListResult listResult) {
        List<ProviderParameters> providers = listResult.getRiskProviders();
        if (providers == null) {
            return null;
        }
        for (ProviderParameters provider : providers) {
            if (containsRiskController(provider.getProviderCode(), provider.getProviderType())) {
                continue;
            }
            RiskProviderController controller = createRiskController(context, provider);
            controller.initialize(context);
            controllers.add(controller);
        }
        return null;
    }

    private List<ProviderParameters> asyncCollectRiskData() {
        List<ProviderParameters> riskData = new ArrayList<>();
        for (RiskProviderController controller : controllers) {
            riskData.add(controller.getRiskData());
        }
        return riskData;
    }

    private RiskProviderController createRiskController(final Context context, ProviderParameters parameters) {
        RiskProviderController controller = RiskProviderController.createFrom(parameters);
        controller.initialize(context);
        return controller;
    }

    private boolean containsRiskController(String code, String type) {
        for (RiskProviderController controller : controllers) {
            if (controller.matches(code, type)) {
                return true;
            }
        }
        return false;
    }
}
