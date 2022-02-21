/*
 *
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 *
 */

package com.payoneer.checkout.exampleshop.util

import androidx.lifecycle.Observer

/**
 * Just to simplify checking whether content has been handled
 *
 * [onUnhandledContent] is called only if contents have not been handled.
 */
class EventObserver(private val onUnhandledContent: () -> Unit) :
    Observer<Event> {
    override fun onChanged(event: Event?) {
        event?.getIfNotHandled()?.let {
            onUnhandledContent()
        }
    }
}