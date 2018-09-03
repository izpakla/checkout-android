/**
 * Copyright(c) 2012-2018 optile GmbH. All Rights Reserved.
 * https://www.optile.net
 *
 * This software is the property of optile GmbH. Distribution  of  this
 * software without agreement in writing is strictly prohibited.
 *
 * This software may not be copied, used or distributed unless agreement
 * has been received in full.
 */

package net.optile.example.checkout;

import android.content.Context;
import android.util.Log;

import net.optile.payment.model.ApplicableNetwork;
import net.optile.payment.model.ListResult;
import net.optile.payment.model.OperationResult;

import net.optile.example.R;
import net.optile.example.util.AppUtils;
import net.optile.payment.network.ChargeConnection;
import net.optile.payment.network.ListConnection;
import net.optile.payment.network.NetworkResponse;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import rx.Single;
import rx.SingleSubscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * CheckoutPresenter responsible for communicating with the 
 * Payment SDK
 */
class CheckoutPresenter {

    public final static String TAG = "payment_CheckoutPresenter";

    private CheckoutView view;

    private Subscription subscription;

    /**
     * Construct a new CheckoutPresenter
     */
    CheckoutPresenter(CheckoutView view) {
        this.view = view;
    }

    /** 
     * Notify the presenter that it should be stopped.
     * Check if there are any pending subscriptions and unsubscribe if needed
     */
    public void onStop() {
        
        if (subscription != null) {
            subscription.unsubscribe();
            subscription = null;
        }
    }

    /**
     * Is this presenter currently making a new list request
     *
     * @return true when active, false otherwise
     */
    boolean isCreateListSessionActive() {
        return subscription != null && !subscription.isUnsubscribed();
    }
    
    /** 
     * Initiate a checkout request in the mobile app.
     *
     * @param context The context needed to obtain system resources
     */
    void checkout(final Context context) {

        if (isCreateListSessionActive()) {
            return;
        }

        final String url  = context.getString(R.string.url);
        final String auth = context.getString(R.string.payment_authorization);

        final String listData   = AppUtils.readRawResource(context.getResources(), R.raw.list);
        final String chargeData = AppUtils.readRawResource(context.getResources(), R.raw.charge);
        
        Single<Void> single = Single.fromCallable(new Callable<Void>() {

                @Override
                public Void call() throws CheckoutException {
                    test(url, auth, listData, chargeData);
                    return null;
                }
            });
        
        this.subscription = single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new SingleSubscriber<Void>() {

                    @Override
                    public void onSuccess(Void parma) {
                        Log.i(TAG, "onSuccess");
                    }

                    @Override
                    public void onError(Throwable error) {
                        Log.i(TAG, "onError: " + error);
                    }
                });
    }

    private void test(String url, String authorization, String listData, String chargeData) throws CheckoutException {

        ListConnection conn = new ListConnection(url);
        
        NetworkResponse response = conn.createPaymentSession(authorization, listData);
        Log.i(TAG, "test createPaymentSession: " + response);
        
        ListResult result = response.getListResult();
        Map<String, URL> links = result.getLinks();
        URL selfURL = links.get("self");

        // Test the self URL and load list session
        if (selfURL != null) {
            response = conn.getListResult(selfURL);            
            Log.i(TAG, "test getListResult: " + response);
        }

        // Test a charge request
        List<ApplicableNetwork> networks = result.getNetworks().getApplicable();
        String code = null;

        for (ApplicableNetwork network : networks) {
            
            if (network.getCode().equals("CARTEBLEUE")) {
                testChargeRequest(network, chargeData);
            }
        }
    }

    
    private void testChargeRequest(ApplicableNetwork network, String chargeData) {

        Log.i(TAG, "testChargeRequest Network[" + network.getCode() + ", " + network.getLabel() + "]");
            
        Map<String, URL> links = network.getLinks();
        URL url = links.get("operation");

        ChargeConnection conn = new ChargeConnection();
        NetworkResponse resp = conn.createCharge(url, chargeData);

        Log.i(TAG, "Charge response: " + resp);
    }
}
