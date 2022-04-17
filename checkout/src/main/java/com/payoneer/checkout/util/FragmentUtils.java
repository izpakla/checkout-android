/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.util;

import java.util.Calendar;

import com.payoneer.checkout.R;
import com.payoneer.checkout.model.AccountMask;

import android.text.TextUtils;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

/**
 * FragmentUtils containing helper methods to show, hide, and remove fragments
 */
public final class FragmentUtils {

    public static void showFragment(final FragmentManager manager, final int resourceId, final Class<? extends Fragment> clazz, final String tag) {
        Fragment fragment = manager.findFragmentByTag(tag);
        if (fragment == null) {
            manager.beginTransaction()
                .setReorderingAllowed(true)
                .add(resourceId, clazz, null, tag)
                .commit();
        } else {
            manager.beginTransaction().show(fragment).commitNow();
        }
    }

    public static void hideFragment(final FragmentManager manager, final String tag) {
        Fragment fragment = manager.findFragmentByTag(tag);
        if (fragment != null) {
            manager.beginTransaction()
                .setReorderingAllowed(true)
                .hide(fragment)
                .commit();
        }
    }

    public static void removeFragment(final FragmentManager manager, final String tag) {
        Fragment fragment = manager.findFragmentByTag(tag);
        if (fragment != null) {
            manager.beginTransaction()
                .setReorderingAllowed(true)
                .remove(fragment)
                .commit();
        }
    }

    public static void showFragment(final FragmentManager manager, final int resourceId, final Fragment fragment, final String tag) {
        manager.beginTransaction()
            .setReorderingAllowed(true)
            .add(resourceId, fragment, tag)
            .commit();
    }
}

