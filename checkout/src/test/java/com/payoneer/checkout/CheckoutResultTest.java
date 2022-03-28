/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import com.payoneer.checkout.model.ErrorInfo;
import com.payoneer.checkout.model.Interaction;
import com.payoneer.checkout.model.InteractionCode;
import com.payoneer.checkout.model.InteractionReason;
import com.payoneer.checkout.model.OperationResult;

import android.os.Parcel;

@RunWith(RobolectricTestRunner.class)
public class CheckoutResultTest {

    @Test
    public void construct_withOperationResult() {
        Interaction interaction = new Interaction(InteractionCode.ABORT, InteractionReason.CLIENTSIDE_ERROR);
        OperationResult operationResult = new OperationResult();
        operationResult.setInteraction(interaction);
        operationResult.setResultInfo("resultInfo");
        CheckoutResult checkoutResult = new CheckoutResult(operationResult);

        assertEquals(operationResult, checkoutResult.getOperationResult());
        assertEquals(interaction, checkoutResult.getInteraction());
        assertEquals("resultInfo", checkoutResult.getResultInfo());
    }

    @Test
    public void construct_withErrorInfo() {
        Interaction interaction = new Interaction(InteractionCode.ABORT, InteractionReason.CLIENTSIDE_ERROR);
        ErrorInfo errorInfo = new ErrorInfo();
        errorInfo.setInteraction(interaction);
        errorInfo.setResultInfo("resultInfo");
        CheckoutResult checkoutResult = new CheckoutResult(errorInfo);

        assertEquals(errorInfo, checkoutResult.getErrorInfo());
        assertEquals("resultInfo", checkoutResult.getResultInfo());
        assertEquals(interaction, checkoutResult.getInteraction());
    }

    @Test
    public void construct_withErrorInfoAndThrowable() {
        Interaction interaction = new Interaction(InteractionCode.ABORT, InteractionReason.CLIENTSIDE_ERROR);
        Throwable cause = new Throwable();
        ErrorInfo errorInfo = new ErrorInfo("resultInfo", interaction);
        CheckoutResult checkoutResult = new CheckoutResult(errorInfo, cause);

        assertEquals(errorInfo, checkoutResult.getErrorInfo());
        assertEquals(cause, checkoutResult.getCause());
        assertEquals("resultInfo", checkoutResult.getResultInfo());
        assertEquals(interaction, checkoutResult.getInteraction());
    }

    @Test
    public void writeToParcel() {
        Interaction interaction = new Interaction(InteractionCode.ABORT, InteractionReason.CLIENTSIDE_ERROR);
        ErrorInfo errorInfo = new ErrorInfo("resultInfo", interaction);
        CheckoutResult writeResult = new CheckoutResult(errorInfo);

        Parcel parcel = Parcel.obtain();
        writeResult.writeToParcel(parcel, 0);

        parcel.setDataPosition(0);
        CheckoutResult readResult = CheckoutResult.CREATOR.createFromParcel(parcel);
        assertEquals(readResult.toString(), writeResult.toString());
    }
}
