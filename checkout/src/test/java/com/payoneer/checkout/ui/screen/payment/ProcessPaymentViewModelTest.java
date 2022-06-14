/*
 *
 *   Copyright (c) 2022 Payoneer Germany GmbH
 *   https://www.payoneer.com
 *
 *   This file is open source and available under the MIT license.
 *   See the LICENSE file for more information.
 *
 */

package com.payoneer.checkout.ui.screen.payment;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import androidx.test.core.app.ApplicationProvider;

import com.payoneer.checkout.CheckoutConfiguration;
import com.payoneer.checkout.payment.PaymentServiceInteractor;
import com.payoneer.checkout.ui.screen.shared.ProgressSettings;
import com.payoneer.checkout.ui.session.PaymentSessionInteractor;
import com.payoneer.checkout.util.Event;
import com.payoneer.checkout.util.LiveDataUtil;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.net.URL;

@RunWith(RobolectricTestRunner.class)
public class ProcessPaymentViewModelTest {

    private final CheckoutConfiguration configuration =
            CheckoutConfiguration.createBuilder(createUrl()).build();
    private final ProcessPaymentViewModel viewModel =
            new ProcessPaymentViewModel(ApplicationProvider.getApplicationContext(), new PaymentSessionInteractor(configuration), new PaymentServiceInteractor());

    @Test
    public void onProcessPaymentResume_setsProgressAndProcessOaymentLiveDataCorrectly() throws InterruptedException {
        viewModel.onProcessPaymentResume();

        Event event = LiveDataUtil.getOrAwaitValue(viewModel.showProcessPaymentFragment()).getIfNotHandled();
        ProgressSettings progressSettings = LiveDataUtil.getOrAwaitValue(viewModel.showProcessPaymentProgress()).getContentIfNotHandled();

        assertNotNull(event);
        assertNotNull(progressSettings);
        assertTrue(progressSettings.visible);
    }

    private URL createUrl() {
        URL url = null;
        try {
            url = new URL("https://example.com/");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }
}