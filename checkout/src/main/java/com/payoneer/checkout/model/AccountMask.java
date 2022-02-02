/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.model;

/**
 * This class is designed to hold account mask for registered payment network.
 */
public class AccountMask {
    /** Simple API, always present */
    private String displayLabel;
    /** Simple API, optional */
    private String holderName;
    /** Simple API, optional */
    private String number;
    /** Simple API, optional */
    private String bankCode;
    /** Simple API, optional */
    private String bankName;
    /** Simple API, optional */
    private String bic;
    /** Simple API, optional */
    private String branch;
    /** Simple API, optional */
    private String city;
    /** Simple API, optional */
    private Integer expiryMonth;
    /** Simple API, optional */
    private Integer expiryYear;
    /** Simple API, optional */
    private String iban;
    /** Simple API, optional */
    private String login;

    public String getDisplayLabel() {
        return displayLabel;
    }

    public void setDisplayLabel(final String displayLabel) {
        this.displayLabel = displayLabel;
    }

    public String getHolderName() {
        return holderName;
    }

    public void setHolderName(final String holderName) {
        this.holderName = holderName;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(final String number) {
        this.number = number;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(final String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(final String bankName) {
        this.bankName = bankName;
    }

    public String getBic() {
        return bic;
    }

    public void setBic(final String bic) {
        this.bic = bic;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(final String branch) {
        this.branch = branch;
    }

    public String getCity() {
        return city;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    public Integer getExpiryMonth() {
        return expiryMonth;
    }

    public void setExpiryMonth(final Integer expiryMonth) {
        this.expiryMonth = expiryMonth;
    }

    public Integer getExpiryYear() {
        return expiryYear;
    }

    public void setExpiryYear(final Integer expiryYear) {
        this.expiryYear = expiryYear;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(final String iban) {
        this.iban = iban;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(final String login) {
        this.login = login;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("AccountMask [");
        if (displayLabel != null) {
            builder.append("displayLabel=").append(displayLabel).append(", ");
        }
        if (holderName != null) {
            builder.append("holderName=").append(holderName).append(", ");
        }
        if (number != null) {
            builder.append("number=").append(number).append(", ");
        }
        if (bankCode != null) {
            builder.append("bankCode=").append(bankCode).append(", ");
        }
        if (bankName != null) {
            builder.append("bankName=").append(bankName).append(", ");
        }
        if (bic != null) {
            builder.append("bic=").append(bic).append(", ");
        }
        if (branch != null) {
            builder.append("branch=").append(branch).append(", ");
        }
        if (city != null) {
            builder.append("city=").append(city).append(", ");
        }
        if (expiryMonth != null) {
            builder.append("expiryMonth=").append(expiryMonth).append(", ");
        }
        if (expiryYear != null) {
            builder.append("expiryYear=").append(expiryYear).append(", ");
        }
        if (iban != null) {
            builder.append("iban=").append(iban).append(", ");
        }
        if (login != null) {
            builder.append("login=").append(login);
        }
        builder.append("]");
        return builder.toString();
    }
}
