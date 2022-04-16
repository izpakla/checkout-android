/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.screen.idlingresource;

import java.util.concurrent.atomic.AtomicBoolean;

import androidx.test.espresso.IdlingResource;

/**
 * A Simple idling resource using a Boolean to define if its idle.
 */
public class SimpleIdlingResource implements IdlingResource {

    private volatile ResourceCallback callback;
    private final AtomicBoolean isIdleNow = new AtomicBoolean(false);
    private final String name;

    public SimpleIdlingResource(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isIdleNow() {
        return isIdleNow.get();
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        this.callback = callback;
    }

    public void reset() {
        this.isIdleNow.set(false);
    }

    public void setIdleState(boolean isIdleNow) {
        this.isIdleNow.set(isIdleNow);
        if (isIdleNow && callback != null) {
            callback.onTransitionToIdle();
        }
    }
}
