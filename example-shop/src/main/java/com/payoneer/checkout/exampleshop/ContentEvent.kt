/*
 *
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 *
 */

package com.payoneer.checkout.exampleshop

import androidx.lifecycle.MutableLiveData


/**
 * Used as a wrapper for data that is exposed via a LiveData that represents an event.
 */
open class ContentEvent<out T>(private val content: T) {

    var hasBeenHandled = false
        private set // Allow external read but not write

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }
}

fun <T> MutableLiveData<ContentEvent<T>>.updateValue(content: T) {
    this.value = ContentEvent(content)
}
