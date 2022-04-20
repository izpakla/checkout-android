/*
 *
 *  * Copyright (c) 2022 Payoneer Germany GmbH
 *  * https://www.payoneer.com
 *  *
 *  * This file is open source and available under the MIT license.
 *  * See the LICENSE file for more information.
 *  *
 */

package com.payoneer.checkout.exampleshop.util

/**
 * Used as a wrapper an event that has no data
 */
open class Event {

    private var hasBeenHandled = false

    fun getIfNotHandled(): Event? {
        return if (hasBeenHandled)
            null
        else {
            hasBeenHandled = true
            this
        }
    }

}