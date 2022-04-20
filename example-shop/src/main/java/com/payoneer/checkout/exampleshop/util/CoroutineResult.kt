/*
 *
 *   Copyright (c) 2022 Payoneer Germany GmbH
 *   https://www.payoneer.com
 *
 *   This file is open source and available under the MIT license.
 *   See the LICENSE file for more information.
 *
 */

package com.payoneer.checkout.exampleshop.util

data class CoroutineResult<out T>(val data: T? = null, val error: Throwable? = null)
