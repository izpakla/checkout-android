/*
 *
 *   Copyright (c) 2022 Payoneer Germany GmbH
 *   https://www.payoneer.com
 *
 *   This file is open source and available under the MIT license.
 *   See the LICENSE file for more information.
 *
 */

package com.payoneer.checkout.ui.screen.list;

import androidx.test.core.app.ApplicationProvider;

import com.payoneer.checkout.CheckoutConfiguration;
import com.payoneer.checkout.account.DeleteAccountInteractor;
import com.payoneer.checkout.payment.PaymentServiceInteractor;
import com.payoneer.checkout.ui.session.PaymentSessionInteractor;

import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.net.URL;

@RunWith(RobolectricTestRunner.class)
public class PaymentListViewModelTest {

    private final CheckoutConfiguration configuration =
            CheckoutConfiguration.createBuilder(createUrl()).build();
    private final PaymentServiceInteractor serviceInteractor = new PaymentServiceInteractor();
    private final PaymentSessionInteractor sessionInteractor = new PaymentSessionInteractor(configuration);
    private final DeleteAccountInteractor accountInteractor = new DeleteAccountInteractor();

    private final PaymentListViewModel viewModel =
            new PaymentListViewModel(ApplicationProvider.getApplicationContext(), sessionInteractor, serviceInteractor, accountInteractor);

    private URL createUrl() {
        try {
            return new URL("https://raw.githubusercontent.com/optile/checkout-android/develop/shared-test/lists/listresult.json");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}