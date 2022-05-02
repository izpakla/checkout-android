/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.examplecheckout;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.After;
import org.junit.Before;

import com.payoneer.checkout.CheckoutActivityResult;
import com.payoneer.checkout.core.PaymentNetworkCodes;
import com.payoneer.checkout.model.AccountInputData;
import com.payoneer.checkout.sharedtest.checkout.TestDataProvider;
import com.payoneer.checkout.sharedtest.service.ListService;
import com.payoneer.checkout.sharedtest.service.ListSettings;
import com.payoneer.checkout.sharedtest.view.UiDeviceHelper;
import com.payoneer.checkout.ui.screen.list.PaymentListActivity;
import com.payoneer.checkout.ui.screen.payment.ProcessPaymentActivity;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.intent.Intents;
import androidx.test.platform.app.InstrumentationRegistry;

public abstract class BaseTest {

    public final static String CHROME_CLOSE_BUTTON = "com.android.chrome:id/close_button";
    private static final String TAG = "BaseTest";

    @Before
    public void beforeTest() {
        Intents.init();
    }

    @After
    public void afterTest() {
        Intents.release();
    }

    protected abstract IdlingResource getResultIdlingResource();

    protected void enterListUrl(String listUrl) {
        onView(withId(R.id.input_listurl)).perform(typeText(listUrl));
    }

    protected void matchResultInteraction(String interactionCode, String interactionReason) {
        onView(withId(R.id.text_interactioncode)).check(matches(withText(interactionCode)));
        onView(withId(R.id.text_interactionreason)).check(matches(withText(interactionReason)));
    }

    protected void matchResultCodeCanceled() {
        String resultCode = CheckoutActivityResult.resultCodeToString(Activity.RESULT_CANCELED);
        this.matchResultCode(resultCode);
    }

    protected void matchResultCode(String resultCode) {
        onView(withId(R.id.text_resultcode)).check(matches(withText(resultCode)));
    }

    protected ListSettings createDefaultListSettings() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        ListSettings settings = new ListSettings(com.payoneer.checkout.examplecheckout.test.R.raw.listtemplate);
        return settings.setAppId(context.getPackageName());
    }

    protected String createListUrl() {
        return createListUrl(createDefaultListSettings());
    }

    protected String createListUrl(ListSettings settings) {
        String paymentApiListUrl = BuildConfig.paymentApiListUrl;
        String merchantCode = BuildConfig.merchantCode;
        String merchantPaymentToken = BuildConfig.merchantPaymentToken;
        Log.i("AAA", "paymentApiListUrl: " + paymentApiListUrl);
        ListService service = ListService.createInstance(createListURL(paymentApiListUrl), merchantCode, merchantPaymentToken);
        return service.newListSelfUrl(settings);
    }

    String registerExpiredAccount(ListSettings settings) {
        ListService service = createListService();
        String networkCode = PaymentNetworkCodes.VISA;
        AccountInputData inputData = TestDataProvider.expiredAccountInputData();
        return service.registerAccount(settings, networkCode, inputData, true, false);
    }

    protected void clickShowPaymentListButton() {
        onView(withId(R.id.button_show_payment_list)).perform(click());
        intended(hasComponent(PaymentListActivity.class.getName()));
    }

    protected void clickChargePresetAccountButton() {
        onView(withId(R.id.button_charge_preset_acount)).perform(click());
        intended(hasComponent(ProcessPaymentActivity.class.getName()));
    }

    protected void register(IdlingResource resource) {
        IdlingRegistry.getInstance().register(resource);
    }

    protected void unregister(IdlingResource resource) {
        IdlingRegistry.getInstance().unregister(resource);
    }

    protected void clickDeviceCollectionPagePageButton(String buttonId) {
        clickBrowserPageButton("simulation of Device Data Collection (DDC) page", buttonId);
    }

    protected void clickCustomerDecisionPageButton(String buttonId) {
        clickBrowserPageButton("customer decision page", buttonId);
    }

    protected void clickBrowserPageButton(String textOnPage, String buttonId) {
        UiDeviceHelper.checkUiObjectContainsText(textOnPage);
        UiDeviceHelper.clickUiObjectByResourceName(buttonId);
    }

    private ListService createListService() {
        String paymentApiListUrl = BuildConfig.paymentApiListUrl;
        String merchantCode = BuildConfig.merchantCode;
        String merchantPaymentToken = BuildConfig.merchantPaymentToken;
        return ListService.createInstance(createListURL(paymentApiListUrl), merchantCode, merchantPaymentToken);
    }

    protected void waitForAppRelaunch() {
        UiDeviceHelper.waitUiObjectHasPackage("com.payoneer.checkout.examplecheckout");
    }

    protected void pressActionBarUp() {
        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click());
    }

    private URL createListURL(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error with creating URL ", e);
        }
        return url;
    }
}
