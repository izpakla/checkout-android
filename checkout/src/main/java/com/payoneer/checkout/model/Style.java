/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.model;

import java.net.URL;

/**
 * This class is designed to hold simplified information to style a redirect pages for express preset transaction.
 */
public class Style {
    /** Optional */
    private String language;
    /** Optional */
    private URL cssOverride;
    /**
     * Challenge Window size the issuer should use to display the challenge. If the input value does not match any of the possible values,
     * a default option specific to payment service provider will be used. Possible values(enumerated in 3DS2 specification):
     * fullPage
     * 250x400
     * 390x400
     * 500x600
     * 600x400
     */
    private String challengeWindowSize;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(final String language) {
        this.language = language;
    }

    public URL getCssOverride() {
        return cssOverride;
    }

    public void setCssOverride(final URL cssOverride) {
        this.cssOverride = cssOverride;
    }

    public String getChallengeWindowSize() {
        return challengeWindowSize;
    }

    public void setChallengeWindowSize(final String challengeWindowSize) {
        this.challengeWindowSize = challengeWindowSize;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Style [");
        if (language != null) {
            builder.append("language=").append(language).append(", ");
        }
        if (cssOverride != null) {
            builder.append("cssOverride=").append(cssOverride).append(", ");
        }
        if (challengeWindowSize != null) {
            builder.append("challengeWindowSize=").append(challengeWindowSize);
        }
        builder.append("]");
        return builder.toString();
    }
}
