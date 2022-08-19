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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.test.core.app.ApplicationProvider;

import com.payoneer.checkout.CheckoutConfiguration;
import com.payoneer.checkout.payment.PaymentServiceInteractor;
import com.payoneer.checkout.ui.screen.shared.ProgressSettings;
import com.payoneer.checkout.ui.session.PaymentSessionInteractor;
import com.payoneer.checkout.util.Event;
import com.payoneer.checkout.util.LiveDataUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.net.MalformedURLException;
import java.net.URL;

@RunWith(RobolectricTestRunner.class)
public class ProcessPaymentViewModelTest {

    private ProcessPaymentViewModel viewModel;

    @Before
    public void setup() throws MalformedURLException {
        URL url = new URL("https://example.com/");
        final CheckoutConfiguration configuration = CheckoutConfiguration.createBuilder(url).build();
        final PaymentSessionInteractor sessionInteractor = new PaymentSessionInteractor(configuration);
        viewModel = new ProcessPaymentViewModel(ApplicationProvider.getApplicationContext(), sessionInteractor, new PaymentServiceInteractor());
    }

    @Test
    public void onProcessPaymentResume_setsProgressAndProcessOaymentLiveDataCorrectly() throws InterruptedException {
        viewModel.onProcessPaymentResume();

        Event event = LiveDataUtil.getOrAwaitValue(viewModel.showProcessPaymentFragment()).getIfNotHandled();
        ProgressSettings progressSettings = LiveDataUtil.getOrAwaitValue(viewModel.showProcessPaymentProgress()).getContentIfNotHandled();

        assertNotNull(event);
        assertNotNull(progressSettings);
        assertTrue(progressSettings.visible);
        Event handledEvent = LiveDataUtil.getOrAwaitValue(viewModel.showProcessPaymentFragment()).getIfNotHandled();
        assertNull(handledEvent);
    }
}