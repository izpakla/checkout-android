/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.model;

/**
 * Input data what could been submitted by payment-page form.
 */
public class AccountInputData {
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
    private String expiryMonth;
    /** Simple API, optional */
    private String expiryYear;
    /** Simple API, optional */
    private String iban;
    /** Simple API, optional */
    private String login;
    /** Simple API, optional */
    private Boolean optIn;
    /** Simple API, optional */
    private String password;
    /** Simple API, optional */
    private String verificationCode;

    /** day of customer's birthday */
    private String customerBirthDay;
    /** month of customer's birthday */
    private String customerBirthMonth;
    /** year of customer's birthday */
    private String customerBirthYear;

    /** id of installment plan */
    private String installmentPlanId;

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

    public String getExpiryMonth() {
        return expiryMonth;
    }

    public void setExpiryMonth(final String expiryMonth) {
        this.expiryMonth = expiryMonth;
    }

    public String getExpiryYear() {
        return expiryYear;
    }

    public void setExpiryYear(final String expiryYear) {
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

    public Boolean getOptIn() {
        return optIn;
    }

    public void setOptIn(final Boolean optIn) {
        this.optIn = optIn;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(final String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public String getCustomerBirthDay() {
        return customerBirthDay;
    }

    public void setCustomerBirthDay(final String customerBirthDay) {
        this.customerBirthDay = customerBirthDay;
    }

    public String getCustomerBirthMonth() {
        return customerBirthMonth;
    }

    public void setCustomerBirthMonth(final String customerBirthMonth) {
        this.customerBirthMonth = customerBirthMonth;
    }

    public String getCustomerBirthYear() {
        return customerBirthYear;
    }

    public void setCustomerBirthYear(final String customerBirthYear) {
        this.customerBirthYear = customerBirthYear;
    }

    public String getInstallmentPlanId() {
        return installmentPlanId;
    }

    public void setInstallmentPlanId(final String installmentPlanId) {
        this.installmentPlanId = installmentPlanId;
    }
}
