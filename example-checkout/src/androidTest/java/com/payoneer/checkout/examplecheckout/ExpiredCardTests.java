/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.examplecheckout;

import static com.payoneer.checkout.sharedtest.checkout.PaymentListHelper.checkHasVisibleExpiredIcon;
import static com.payoneer.checkout.sharedtest.checkout.PaymentListHelper.matchesPaymentCardSubtitle;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.payoneer.checkout.sharedtest.checkout.PaymentListHelper;
import com.payoneer.checkout.sharedtest.service.ListSettings;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

@RunWith(AndroidJUnit4.class)
@LargeTest
public final class ExpiredCardTests extends BaseKotlinTest {

    @Test
    public void testExpiredCard() {
        ListSettings settings = createDefaultListSettings();
        String registrationId = registerExpiredAccount(settings);

        settings.setRegistrationId(registrationId);
        enterListUrl(createListUrl(settings));
        clickShowPaymentListButton();

        int accountCardIndex = 1;
        PaymentListHelper.waitForPaymentListLoaded(1);
        PaymentListHelper.openPaymentListCard(accountCardIndex, "card.account");

        matchesPaymentCardSubtitle(accountCardIndex, "12 / 19");
        checkHasVisibleExpiredIcon(accountCardIndex);
    }
}

