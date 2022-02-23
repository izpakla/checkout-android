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

/**
 * A generic class that holds a value with its loading status.
 * @param T is the data from the API call
 * @param status is the current status of the call
 * @param message is the error message
 */
data class Resource<out T>(val status: Status, val data: T? = null, val message: String? = null) {
    companion object {
        fun <T> success(data: T): Resource<T> {
            return Resource(Status.SUCCESS, data)
        }

        fun <T> error(message: String?): Resource<T> {
            return Resource(Status.ERROR, message = message)
        }

        fun <T> loading(): Resource<T> {
            return Resource(Status.LOADING)
        }
    }

    /**
     * Status of a resource that is provided to the UI.
     */
    sealed class Status {
        object SUCCESS : Status()
        object ERROR : Status()
        object LOADING : Status()
    }
}