/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */
package com.payoneer.checkout.exampleshop.shared

/**
 * Generic ShopException
 */
class ShopException(detailMessage: String?, cause: Throwable?) : Exception(detailMessage, cause)