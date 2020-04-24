/*
 * Copyright (c) 2019 optile GmbH
 * https://www.optile.net
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package net.optile.payment.ui.service;

import net.optile.payment.localization.Localization;

/**
 * Listener to be called by the LocalizationService to inform about request updates.
 */
public interface LocalizationLoaderListener {

    /**
     * Called when the Localization files were successfully loaded by the service.
     *
     * @param localization contains the loaded localizations
     */
    void onLocalizationSuccess(Localization localization);

    /**
     * Called when an error occurred during loading the localization files.
     *
     * @param cause describing the reason of failure
     */
    void onLocalizationError(Throwable cause);
}