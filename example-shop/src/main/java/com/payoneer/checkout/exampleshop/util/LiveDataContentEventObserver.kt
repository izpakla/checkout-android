/*
 *
 *  Copyright (c) 2022 Payoneer Germany GmbH
 *  https://www.payoneer.com
 *
 *  This file is open source and available under the MIT license.
 *  See the LICENSE file for more information.
 *
 */

package com.payoneer.checkout.exampleshop.util

import androidx.lifecycle.Observer

class LiveDataContentEventObserver<T>(private val onEventUnhandledContent: (T) -> Unit) :
    Observer<ContentEvent<T>> {
    override fun onChanged(event: ContentEvent<T>?) {
        event?.getContentIfNotHandled()?.let { value ->
            onEventUnhandledContent(value)
        }
    }
}