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

import static com.payoneer.checkout.redirect.RedirectService.INTERACTION_CODE;
import static com.payoneer.checkout.redirect.RedirectService.INTERACTION_REASON;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import androidx.test.core.app.ApplicationProvider;

import com.payoneer.checkout.CheckoutConfiguration;
import com.payoneer.checkout.CheckoutResult;
import com.payoneer.checkout.account.DeleteAccountInteractor;
import com.payoneer.checkout.model.Interaction;
import com.payoneer.checkout.model.Parameter;
import com.payoneer.checkout.model.PresetAccount;
import com.payoneer.checkout.model.Redirect;
import com.payoneer.checkout.payment.PaymentServiceInteractor;
import com.payoneer.checkout.ui.model.PaymentSession;
import com.payoneer.checkout.ui.model.PresetCard;
import com.payoneer.checkout.ui.session.PaymentSessionInteractor;
import com.payoneer.checkout.util.ContentEvent;
import com.payoneer.checkout.util.LiveDataUtil;
import com.payoneer.checkout.util.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class PaymentListViewModelTest {

    private final CheckoutConfiguration configuration =
            CheckoutConfiguration.createBuilder(createUrl()).build();
    private final PaymentServiceInteractor serviceInteractor = new PaymentServiceInteractor();
    private final PaymentSessionInteractor sessionInteractor = new PaymentSessionInteractor(configuration);
    private final DeleteAccountInteractor accountInteractor = new DeleteAccountInteractor();

    private final PaymentListViewModel viewModel =
            new PaymentListViewModel(ApplicationProvider.getApplicationContext(), sessionInteractor, serviceInteractor, accountInteractor);

    @Test
    public void loadPaymentSession_setsLoadingLiveDataCorrectly() throws InterruptedException {
        viewModel.loadPaymentSession();

        Resource<PaymentSession> showPaymentSession = LiveDataUtil.getOrAwaitValue(viewModel.showPaymentSession());

        assertNotNull(showPaymentSession);
        assertEquals(showPaymentSession.getStatus(), Resource.LOADING);
    }

    @Test
    public void processPaymentCard_shouldSetCorrectDataForPresetCard() throws InterruptedException {
        List<Parameter> parameters = new ArrayList<>();
        Parameter codeParam = new Parameter();
        codeParam.setName(INTERACTION_CODE);
        codeParam.setValue("code");
        Parameter reasonParam = new Parameter();
        reasonParam.setName(INTERACTION_REASON);
        reasonParam.setValue("reason");
        parameters.add(codeParam);
        parameters.add(reasonParam);

        Redirect redirect = new Redirect();
        redirect.setParameters(parameters);
        PresetAccount account = new PresetAccount();
        account.setRedirect(redirect);

        PresetCard card = new PresetCard(account, "", null);

        viewModel.processPaymentCard(card, null);

        ContentEvent<CheckoutResult> resultContentEvent = LiveDataUtil.getOrAwaitValue(viewModel.closeWithCheckoutResult());
        Interaction interaction = resultContentEvent.getContentIfNotHandled().getInteraction();
        assertNotNull(interaction);
        assertEquals(interaction.getCode(), "code");
        assertEquals(interaction.getReason(), "reason");
    }

    private URL createUrl() {
        try {
            return new URL("https://raw.githubusercontent.com/optile/checkout-android/develop/shared-test/lists/listresult.json");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}