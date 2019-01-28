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

package net.optile.payment.ui.theme;

import net.optile.payment.R;

/**
 * Class for holding the DialogParameters for the PaymentTheme
 * These parameters may be used to theme the message and date popup dialogs.
 */
public final class DialogParameters {

    private final int dialogTheme;
    private final int dateTitleStyle;
    private final int dateSubtitleStyle;
    private final int messageTitleStyle;
    private final int messageDetailsStyle;
    private final int messageDetailsNoTitleStyle;
    private final int buttonLabelStyle;
    private final int imageLabelStyle;
    private final int snackbarTextStyle;
    
    private DialogParameters(Builder builder) {
        this.dialogTheme = builder.dialogTheme;
        this.dateTitleStyle = builder.dateTitleStyle;
        this.dateSubtitleStyle = builder.dateSubtitleStyle;
        this.messageTitleStyle = builder.messageTitleStyle;
        this.messageDetailsStyle = builder.messageDetailsStyle;
        this.messageDetailsNoTitleStyle = builder.messageDetailsNoTitleStyle;
        this.buttonLabelStyle = builder.buttonLabelStyle;
        this.imageLabelStyle = builder.imageLabelStyle;
        this.snackbarTextStyle = builder.snackbarTextStyle;
    }

    public static Builder createBuilder() {
        return new Builder();
    }

    public static DialogParameters createDefault() {
        return createBuilder().
            setDialogTheme(R.style.PaymentThemeDialog).
            setDateTitleStyle(R.style.PaymentText_Medium_Bold).
            setDateSubtitleStyle(R.style.PaymentText_Small_Bold).
            setMessageTitleStyle(R.style.PaymentText_Large_Bold).
            setMessageDetailsStyle(R.style.PaymentText_Medium_Gray).
            setMessageDetailsNoTitleStyle(R.style.PaymentText_Medium_Bold_Gray).
            setButtonLabelStyle(R.style.PaymentText_Small_Bold_Primary).
            setImageLabelStyle(R.style.PaymentText_Tiny).
            setSnackbarTextStyle(R.style.PaymentText_Small_Light).
            build();
    }

    public int getDialogTheme() {
        return dialogTheme;
    }

    public int getDateTitleStyle() {
        return dateTitleStyle;
    }

    public int getDateSubtitleStyle() {
        return dateSubtitleStyle;
    }

    public int getMessageTitleStyle() {
        return messageTitleStyle;
    }

    public int getMessageDetailsStyle() {
        return messageDetailsStyle;
    }

    public int getMessageDetailsNoTitleStyle() {
        return messageDetailsNoTitleStyle;
    }

    public int getButtonLabelStyle() {
        return buttonLabelStyle;
    }

    public int getImageLabelStyle() {
        return imageLabelStyle;
    }

    public int getSnackbarTextStyle() {
        return snackbarTextStyle;
    }
    
    public final static class Builder {
        int dialogTheme;
        int messageTitleStyle;
        int messageDetailsStyle;
        int messageDetailsNoTitleStyle;
        int dateTitleStyle;
        int dateSubtitleStyle;
        int buttonLabelStyle;
        int imageLabelStyle;
        int snackbarTextStyle;
        
        Builder() {
        }

        public Builder setDialogTheme(int dialogTheme) {
            this.dialogTheme = dialogTheme;
            return this;
        }

        public Builder setMessageTitleStyle(int messageTitleStyle) {
            this.messageTitleStyle = messageTitleStyle;
            return this;
        }

        public Builder setMessageDetailsStyle(int messageDetailsStyle) {
            this.messageDetailsStyle = messageDetailsStyle;
            return this;
        }

        public Builder setMessageDetailsNoTitleStyle(int messageDetailsNoTitleStyle) {
            this.messageDetailsNoTitleStyle = messageDetailsNoTitleStyle;
            return this;
        }

        public Builder setDateTitleStyle(int dateTitleStyle) {
            this.dateTitleStyle = dateTitleStyle;
            return this;
        }

        public Builder setDateSubtitleStyle(int dateSubtitleStyle) {
            this.dateSubtitleStyle = dateSubtitleStyle;
            return this;
        }

        public Builder setButtonLabelStyle(int buttonLabelStyle) {
            this.buttonLabelStyle = buttonLabelStyle;
            return this;
        }

        public Builder setImageLabelStyle(int imageLabelStyle) {
            this.imageLabelStyle = imageLabelStyle;
            return this;
        }

        public Builder setSnackbarTextStyle(int snackbarTextStyle) {
            this.snackbarTextStyle = snackbarTextStyle;
            return this;
        }
        
        public DialogParameters build() {
            return new DialogParameters(this);
        }
    }
}
