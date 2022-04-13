/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.examplecheckout;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.payoneer.checkout.sharedtest.view.PaymentMatchers.isViewInPaymentCard;

import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.payoneer.checkout.sharedtest.checkout.PaymentListHelper;
import com.payoneer.checkout.sharedtest.service.ListSettings;

import android.view.View;
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

        Matcher<View> list = withId(R.id.recyclerview_paymentlist);
        onView(list).check(matches(isViewInPaymentCard(accountCardIndex, withText("12 / 19"), R.id.text_subtitle)));
        onView(list).check(matches(isViewInPaymentCard(accountCardIndex, withId(R.id.image_expired_icon), R.id.image_expired_icon)));
    }
}
