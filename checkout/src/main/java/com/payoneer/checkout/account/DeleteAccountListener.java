/*
 * Copyright (c) 2022 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.account;

import com.payoneer.checkout.model.OperationResult;

/**
 * Listener to be called by the DeleteAccountService to inform about delete updates.
 */
public interface DeleteAccountListener {

    /**
     * Called when the account was successfully deleted.
     *
     * @param operationResult containing the result of the deletion request.
     */
    void onDeleteAccountSuccess(OperationResult operationResult);

    /**
     * Called when an error occurred while deleting the account.
     *
     * @param cause describing the reason of failure
     */
    void onDeleteAccountError(Throwable cause);
}
