/*
 *
 *   Copyright (c) 2022 Payoneer Germany GmbH
 *   https://www.payoneer.com
 *
 *   This file is open source and available under the MIT license.
 *   See the LICENSE file for more information.
 *
 */

package com.payoneer.checkout.util;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class LiveDataUtil {

    private static final int LATCH_WAIT_TIMEOUT = 2;

    @SuppressWarnings("unchecked")
    public static <T> T getOrAwaitValue(LiveData<T> liveData) throws InterruptedException {
        final T[] data = (T[]) new Object[]{null};
        CountDownLatch latch = new CountDownLatch(1);

        Observer<T> observer1 = new Observer<T>() {
            @Override
            public void onChanged(T t) {
                data[0] = t;
                latch.countDown();
                liveData.removeObserver(this);
            }
        };

        liveData.observeForever(observer1);

        // Don't wait indefinitely if the LiveData is not set.
        if (!latch.await(LATCH_WAIT_TIMEOUT, TimeUnit.SECONDS)) {
            throw new InterruptedException("LiveData value was never set.");
        }

        return data[0];
    }
}
