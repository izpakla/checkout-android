/*
 * Copyright (c) 2019 optile GmbH
 * https://www.optile.net
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package net.optile.example.demo;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.times;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static net.optile.payment.test.view.PaymentActions.actionOnViewInWidget;
import static net.optile.payment.test.view.PaymentMatchers.isCardWithTestId;

import java.io.IOException;

import org.hamcrest.Matcher;
import org.json.JSONException;

import android.view.View;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import net.optile.example.demo.checkout.CheckoutActivity;
import net.optile.example.demo.confirm.ConfirmActivity;
import net.optile.example.demo.summary.SummaryActivity;
import net.optile.payment.test.service.ListService;
import net.optile.payment.test.view.ActivityHelper;
import net.optile.payment.test.view.PaymentActions;
import net.optile.payment.ui.page.ChargePaymentActivity;
import net.optile.payment.ui.page.PaymentListActivity;

public class AbstractTest {

    public final static long CHROME_TIMEOUT = 20000;

    void openPaymentList(boolean presetFirst) throws IOException, JSONException {
        String listUrl = ListService.createListUrl(net.optile.example.demo.test.R.raw.listtemplate, presetFirst);

        onView(withId(R.id.layout_settings)).check(matches(isDisplayed()));
        onView(withId(R.id.input_listurl)).perform(typeText(listUrl));
        onView(withId(R.id.button_settings)).perform(click());
        intended(hasComponent(CheckoutActivity.class.getName()));
        onView(withId(R.id.button_checkout)).perform(PaymentActions.scrollToView(), click());
        waitForPaymentListLoaded(1);
    }

    void waitForPaymentListLoaded(int count) {
        intended(hasComponent(PaymentListActivity.class.getName()), times(count));
        PaymentListActivity listActivity = (PaymentListActivity) ActivityHelper.getCurrentActivity();
        IdlingResource loadIdlingResource = listActivity.getLoadIdlingResource();

        register(loadIdlingResource);
        onView(withId(R.id.recyclerview_paymentlist)).check(matches(isDisplayed()));
        unregister(loadIdlingResource);
    }

    void waitForSummaryPageLoaded() {
        intended(hasComponent(SummaryActivity.class.getName()));
        SummaryActivity summaryActivity = (SummaryActivity) ActivityHelper.getCurrentActivity();
        IdlingResource loadIdlingResource = summaryActivity.getLoadIdlingResource();
        register(loadIdlingResource);
        onView(withId(R.id.layout_coordinator)).check(matches(isDisplayed()));
        unregister(loadIdlingResource);
    }


    void openPaymentCard(int cardIndex, String cardTestId) {
        PaymentListActivity listActivity = (PaymentListActivity) ActivityHelper.getCurrentActivity();
        Matcher<View> list = withId(R.id.recyclerview_paymentlist);

        onView(list).check(matches(isCardWithTestId(cardIndex, cardTestId)));
        onView(list).perform(actionOnItemAtPosition(cardIndex, click()));
    }

    void fillCreditCardData(int cardIndex) {
        PaymentListActivity listActivity = (PaymentListActivity) ActivityHelper.getCurrentActivity();
        Matcher<View> list = withId(R.id.recyclerview_paymentlist);

        onView(list).perform(actionOnViewInWidget(cardIndex, typeText("4111111111111111"), "number", R.id.textinputedittext));
        onView(list).perform(actionOnViewInWidget(cardIndex, typeText("John Doe"), "holderName", R.id.textinputedittext));
        onView(list).perform(actionOnViewInWidget(cardIndex, typeText("1245"), "expiryDate", R.id.textinputedittext));
        onView(list).perform(actionOnViewInWidget(cardIndex, typeText("123"), "verificationCode", R.id.textinputedittext));
        Espresso.closeSoftKeyboard();
    }

    void waitForChargeCompleted() {
        intended(hasComponent(ChargePaymentActivity.class.getName()));
        onView(withId(R.id.layout_chargepayment)).check(matches(isDisplayed()));
        ChargePaymentActivity chargeActivity = (ChargePaymentActivity) ActivityHelper.getCurrentActivity();
        IdlingResource chargeIdlingResource = chargeActivity.getChargeIdlingResource();
        register(chargeIdlingResource);

        intended(hasComponent(ConfirmActivity.class.getName()));
        onView(withId(R.id.layout_confirm)).check(matches(isDisplayed()));
        unregister(chargeIdlingResource);
    }

    IdlingResource clickCardButton(int cardIndex) {
        PaymentListActivity listActivity = (PaymentListActivity) ActivityHelper.getCurrentActivity();
        IdlingResource closeIdlingResource = listActivity.getCloseIdlingResource();
        onView(withId(R.id.recyclerview_paymentlist)).perform(actionOnViewInWidget(cardIndex, click(), "buttonWidget", R.id.button));
        return register(closeIdlingResource);
    }

    IdlingResource register(IdlingResource resource) {
        IdlingRegistry.getInstance().register(resource);
        return resource;
    }

    IdlingResource unregister(IdlingResource resource) {
        IdlingRegistry.getInstance().unregister(resource);
        return resource;
    }
}