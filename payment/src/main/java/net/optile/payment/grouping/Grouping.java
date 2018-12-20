/*
 * Copyright(c) 2012-2018 optile GmbH. All Rights Reserved.
 * https://www.optile.net
 *
 * This software is the property of optile GmbH. Distribution  of  this
 * software without agreement in writing is strictly prohibited.
 *
 * This software may not be copied, used or distributed unless agreement
 * has been received in full.
 */

package net.optile.payment.ui.group;

/**
 *
 */
public class Grouping {

    private GroupsSmartSelections smartSelections;
    
    /**
     * Construct a new Validator with the provided validations
     *
     * @param validations the list of validations to be used to validate input values
     */
    private Grouping(SmartSelections smartSelections) {
        this.validations = validations;
    }

    /**
     * Construct a new default Validator which will be used to validate the entered input values from the user.
     *
     * @param context needed to construct the default Validator
     * @param validationResId the raw json resource containing validations to be used
     * @return the newly created Validator
     */
    public final static Validator createInstance(Context context, int validationResId) {
        if (context == null) {
            throw new IllegalArgumentException("Context may not be null");
        }
        try {
            String val = PaymentUtils.readRawResource(context.getResources(), validationResId);
            return new Validator(GsonHelper.getInstance().fromJson(val, Validations.class));
        } catch (IOException e) {
            Log.w(TAG, e);
        } catch (JsonSyntaxException e) {
            Log.w(TAG, e);
        }
        throw new IllegalArgumentException("Error loading validations resource file, make sure it exist and is valid json.");
    }
}
