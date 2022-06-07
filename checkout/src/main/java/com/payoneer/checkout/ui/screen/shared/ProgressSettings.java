/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.screen.shared;

public final class ProgressSettings {
    public final boolean visible;
    public final String header;
    public final String info;

    public ProgressSettings(final boolean visible) {
        this(visible, null, null);
    }

    public ProgressSettings(final boolean visible, final String header, final String info) {
        this.visible = visible;
        this.header = header;
        this.info = info;
    }
}
