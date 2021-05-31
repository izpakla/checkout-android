/*
 * Copyright (c) 2021 Payoneer Germany GmbH
 * https://payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.list;

import java.util.ArrayList;
import java.util.List;

/**
 * Class containing ListItems with a selected index
 */
final class PaymentItemList {

    private final List<ListItem> items;
    private int selectedIndex;

    PaymentItemList() {
        this.items = new ArrayList<>();
    }

    List<ListItem> getItems() {
        return items;
    }

    int getSelectedIndex() {
        return selectedIndex;
    }

    void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    ListItem getItem(int index) {
        return index >= 0 && index < items.size() ? items.get(index) : null;
    }
    
    void clear() {
        selectedIndex = -1;
        items.clear();
    }

    boolean validIndex(int index) {
        return index >= 0 && index < items.size();
    }
    
    ListItem getItemWithViewType(int viewType) {
        for (ListItem item : items) {
            if (item.viewType == viewType) {
                return item;
            }
        }
        return null;
    }

    int getItemCount() {
        return items.size();
    }

    int getItemViewType(int index) {
        return items.get(index).viewType;
    }

    void addItem(ListItem item, boolean preselected) {
        items.add(item);
        if (preselected) {
            selectedIndex = items.size() - 1;
        }
    }
}
