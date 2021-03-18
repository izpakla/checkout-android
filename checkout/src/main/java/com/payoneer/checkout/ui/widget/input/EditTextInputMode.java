/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.widget.input;

import com.google.android.material.textfield.TextInputEditText;

/**
 * Base class for defining the mode for the TextInputWidget
 */
public abstract class EditTextInputMode {

    final int maxLength;
    final int groupSize;
    EditTextWatcher textWatcher;

    EditTextInputMode(int maxLength, int groupSize) {
        this.maxLength = maxLength;
        this.groupSize = groupSize;
    }

    /**
     * Normalize the value if needed for the given mode
     *
     * @param value to be normalized
     * @return the normalized value
     */
    public String normalize(String value) {
        return value;
    }

    /**
     * Get the maxLength when grouping is applied
     *
     * @return the maxLength for grouping
     */
    public int getMaxLengthForGrouping() {
        if (groupSize == 0 || maxLength == 0) {
            return maxLength;
        }
        return maxLength + (maxLength / groupSize);
    }

    /**
     * Reset this mode when it is not used anymore
     */
    public void reset() {
        if (textWatcher != null) {
            textWatcher.reset();
        }
    }

    /**
     * Apply the mode to the TextInputEditText
     *
     * @param editText to apply the mode to
     */
    public abstract void apply(TextInputEditText editText);
}


