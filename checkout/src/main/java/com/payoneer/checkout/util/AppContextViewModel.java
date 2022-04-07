/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.util;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

/**
 * View model for holding the application context. In principle this is the same as the AndroidViewModel
 * except that a library does not have access to the Application class and therefore is bound to use the
 * application context which may be obtained from e.g. Activity.getApplicationContext()
 */
public class AppContextViewModel extends ViewModel {

    private final Context applicationContext;

    public AppContextViewModel(@NonNull final Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    public Context getApplicationContext() {
        return applicationContext;
    }
}
