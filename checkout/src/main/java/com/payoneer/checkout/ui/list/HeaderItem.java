/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.list;

/**
 * Class representing a header in the PaymentList
 */
final class HeaderItem extends ListItem {

    final String title;

    HeaderItem(int viewType, String title) {
        super(viewType);
        this.title = title;
    }
}
