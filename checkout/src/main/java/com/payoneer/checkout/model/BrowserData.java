/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.model;

/**
 * Customer web browser data.
 */
public class BrowserData {

    /**
     * Java enabled flag. The ability of the cardholder browser to execute Java.
     * Value is returned from the navigator.javaEnabled property.
     */
    private Boolean javaEnabled;
    /**
     * This value represents the browser language as defined in IETF BCP47. The value is limited to 1-8 characters.
     * Value is returned from navigator.language property.
     */
    private String language;
    /**
     * Color depth. The value represents the bit depth of the color palette for displaying images, in bits per pixel.
     * Obtained from cardholder browser using the screen.colorDepth property.
     */
    private Integer colorDepth;
    /** Timezone */
    private String timezone;
    /** Browser screen height. Total height of the cardholder's screen in pixels. */
    private Integer browserScreenHeight;
    /** Browser screen width. Total width of the cardholder's screen in pixels. */
    private Integer browserScreenWidth;

    public Boolean getJavaEnabled() {
        return javaEnabled;
    }

    public void setJavaEnabled(final Boolean javaEnabled) {
        this.javaEnabled = javaEnabled;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(final String language) {
        this.language = language;
    }

    public Integer getColorDepth() {
        return colorDepth;
    }

    public void setColorDepth(final Integer colorDepth) {
        this.colorDepth = colorDepth;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(final String timezone) {
        this.timezone = timezone;
    }

    public Integer getBrowserScreenHeight() {
        return browserScreenHeight;
    }

    public void setBrowserScreenHeight(final Integer browserScreenHeight) {
        this.browserScreenHeight = browserScreenHeight;
    }

    public Integer getBrowserScreenWidth() {
        return browserScreenWidth;
    }

    public void setBrowserScreenWidth(final Integer browserScreenWidth) {
        this.browserScreenWidth = browserScreenWidth;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("BrowserData [");
        if (javaEnabled != null) {
            builder.append("javaEnabled=").append(javaEnabled).append(", ");
        }
        if (language != null) {
            builder.append("language=").append(language).append(", ");
        }
        if (colorDepth != null) {
            builder.append("colorDepth=").append(colorDepth).append(", ");
        }
        if (timezone != null) {
            builder.append("timezone=").append(timezone).append(", ");
        }
        if (browserScreenHeight != null) {
            builder.append("browserScreenHeight=").append(browserScreenHeight).append(", ");
        }
        if (browserScreenWidth != null) {
            builder.append("browserScreenWidth=").append(browserScreenWidth);
        }
        builder.append("]");
        return builder.toString();
    }
}
