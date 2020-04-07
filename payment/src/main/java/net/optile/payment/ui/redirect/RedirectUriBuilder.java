/*
 * Copyright (c) 2019 optile GmbH
 * https://www.optile.net
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package net.optile.payment.ui.redirect;

import java.util.List;

import android.net.Uri;
import net.optile.payment.model.Parameter;
import net.optile.payment.model.Redirect;

/**
 * Class for constructing a Uri from a Redirect model class
 */
public final class RedirectUriBuilder {

    /** 
     * Construct a Uri from the provided redirect
     * 
     * @param redirect to be converted into a Uri 
     * @return the created Uri 
     */
    public static Uri createUri(Redirect redirect) {
        Uri uri = Uri.parse(redirect.getUrl().toString());
        List<Parameter> params = redirect.getParameters();
        if (params == null || params.size() == 0) {
            return uri;
        }
        Uri.Builder builder = uri.buildUpon();
        for (Parameter param : params) {
            builder.appendQueryParameter(param.getName(), param.getValue());
        }
        return builder.build();
    }
}
