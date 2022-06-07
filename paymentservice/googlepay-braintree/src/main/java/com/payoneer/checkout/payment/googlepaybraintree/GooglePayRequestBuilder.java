/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.payment.googlepaybraintree;

import com.braintreepayments.api.GooglePayRequest;
import com.google.android.gms.wallet.TransactionInfo;
import com.google.android.gms.wallet.WalletConstants;
import com.payoneer.checkout.core.PaymentException;
import com.payoneer.checkout.model.OperationResult;
import com.payoneer.checkout.util.PaymentUtils;

import android.text.TextUtils;

/**
 * Class for building the GooglePayRequest from the data provided in the operationResult
 */
final class GooglePayRequestBuilder {

    private final static String AMOUNT_IN_MAJOR_UNITS = "amountInMajorUnits";
    private final static String CURRENCY_CODE = "currencyCode";
    private final static String ENVIRONMENT = "environment";

    private String environment;
    private String amountInMajorUnits;
    private String currencyCode;

    static final GooglePayRequest of(final OperationResult operationResult) throws PaymentException {
        return new GooglePayRequestBuilder()
            .setEnvironment(PaymentUtils.getProviderParameterValue(ENVIRONMENT, operationResult))
            .setAmountInMajorUnits(PaymentUtils.getProviderParameterValue(AMOUNT_IN_MAJOR_UNITS, operationResult))
            .setCurrencyCode(PaymentUtils.getProviderParameterValue(CURRENCY_CODE, operationResult))
            .build();
    }

    GooglePayRequest build() throws PaymentException {
        if (TextUtils.isEmpty((environment))) {
            throw new PaymentException("Missing GooglePayBraintree [" + ENVIRONMENT + "] parameter");
        }
        if (TextUtils.isEmpty((amountInMajorUnits))) {
            throw new PaymentException("Missing GooglePayBraintree [" + AMOUNT_IN_MAJOR_UNITS + "] parameter");
        }
        if (TextUtils.isEmpty((currencyCode))) {
            throw new PaymentException("Missing GooglePayBraintree [" + CURRENCY_CODE + "] parameter");
        }
        GooglePayRequest googlePayRequest = new GooglePayRequest();
        googlePayRequest.setTransactionInfo(TransactionInfo.newBuilder()
            .setTotalPrice(amountInMajorUnits)
            .setTotalPriceStatus(WalletConstants.TOTAL_PRICE_STATUS_FINAL)
            .setCurrencyCode(currencyCode)
            .build());
        googlePayRequest.setBillingAddressRequired(true);
        googlePayRequest.setEnvironment(environment);
        return googlePayRequest;
    }

    GooglePayRequestBuilder setEnvironment(final String environment) {
        this.environment = environment;
        return this;
    }

    GooglePayRequestBuilder setAmountInMajorUnits(final String amountInMajorUnits) {
        this.amountInMajorUnits = amountInMajorUnits;
        return this;
    }

    GooglePayRequestBuilder setCurrencyCode(final String currencyCode) {
        this.currencyCode = currencyCode;
        return this;
    }
}
