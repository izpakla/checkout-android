/*
 *
 *  Copyright (c) 2021 Payoneer Germany GmbH
 *  https://www.payoneer.com
 *
 *  This file is open source and available under the MIT license.
 *  See the LICENSE file for more information.
 *
 */

package com.payoneer.checkout.examplecheckout.utils

import android.widget.TextView
import com.payoneer.checkout.examplecheckout.R

fun TextView.setLabel(message: String) {
    val label = if (message.isEmpty()) this.context.getString(R.string.empty_label) else message
    this.text = label
}