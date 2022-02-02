/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.model;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;

/**
 * Payment plan to pay by schedule.
 */
public class InstallmentsPlan {
    /** unique plan id. */
    private String id;
    /** Collection of installment's info (mandatory) */
    private List<InstallmentItem> schedule;
    /** An array of possible payment days (optional) */
    private List<Integer> dueDays;

    /** Currency value (mandatory) */
    private String currency;

    /** The interest amount. */
    private BigDecimal interestAmount;
    /** Fee for opening up an installment plan (optional) */
    private BigDecimal installmentSetupFee;
    /** Constant periodic fee for each installment item  (optional) */
    private BigDecimal installmentPeriodicFee;
    /**
     * The total fee for the installment payment (or serviceChargeAmount) (mandatory).
     * Includes all periodic fees and the installment set-up fee.
     */
    private BigDecimal installmentFee;
    /** The total transaction amount after calculation including all fees and interest (mandatory) */
    private BigDecimal totalAmount;

    /** The interest rate per year in percentages (Nominalzins or Sollzins) (mandatory) */
    private BigDecimal nominalInterestRate;
    /** The effective interest rate per year in percentages (Effektivzins) (mandatory) */
    private BigDecimal effectiveInterestRate;

    /** An URL to the Credit Information document (optional) */
    private URL creditInformationUrl;
    /** An URL to terms and conditions information document (optional) */
    private URL termsAndConditionsUrl;
    /** An URL to the data privacy consent document (optional) */
    private URL dataPrivacyConsentUrl;
    /** An URL to the installment plan logo (optional) */
    private URL logoUrl;
    /** Description of the installments plan (optional) */
    private String description;
    /** Number of installments in the installments plan (optional) */
    private Integer numberOfInstallments;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public List<InstallmentItem> getSchedule() {
        return schedule;
    }

    public void setSchedule(final List<InstallmentItem> schedule) {
        this.schedule = schedule;
    }

    public List<Integer> getDueDays() {
        return dueDays;
    }

    public void setDueDays(final List<Integer> dueDays) {
        this.dueDays = dueDays;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(final String currency) {
        this.currency = currency;
    }

    public BigDecimal getInterestAmount() {
        return interestAmount;
    }

    public void setInterestAmount(final BigDecimal interestAmount) {
        this.interestAmount = interestAmount;
    }

    public BigDecimal getInstallmentSetupFee() {
        return installmentSetupFee;
    }

    public void setInstallmentSetupFee(final BigDecimal installmentSetupFee) {
        this.installmentSetupFee = installmentSetupFee;
    }

    public BigDecimal getInstallmentPeriodicFee() {
        return installmentPeriodicFee;
    }

    public void setInstallmentPeriodicFee(final BigDecimal installmentPeriodicFee) {
        this.installmentPeriodicFee = installmentPeriodicFee;
    }

    public BigDecimal getInstallmentFee() {
        return installmentFee;
    }

    public void setInstallmentFee(final BigDecimal installmentFee) {
        this.installmentFee = installmentFee;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(final BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getNominalInterestRate() {
        return nominalInterestRate;
    }

    public void setNominalInterestRate(final BigDecimal nominalInterestRate) {
        this.nominalInterestRate = nominalInterestRate;
    }

    public BigDecimal getEffectiveInterestRate() {
        return effectiveInterestRate;
    }

    public void setEffectiveInterestRate(final BigDecimal effectiveInterestRate) {
        this.effectiveInterestRate = effectiveInterestRate;
    }

    public URL getCreditInformationUrl() {
        return creditInformationUrl;
    }

    public void setCreditInformationUrl(final URL creditInformationUrl) {
        this.creditInformationUrl = creditInformationUrl;
    }

    public URL getTermsAndConditionsUrl() {
        return termsAndConditionsUrl;
    }

    public void setTermsAndConditionsUrl(final URL termsAndConditionsUrl) {
        this.termsAndConditionsUrl = termsAndConditionsUrl;
    }

    public URL getDataPrivacyConsentUrl() {
        return dataPrivacyConsentUrl;
    }

    public void setDataPrivacyConsentUrl(final URL dataPrivacyConsentUrl) {
        this.dataPrivacyConsentUrl = dataPrivacyConsentUrl;
    }

    public URL getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(final URL logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public Integer getNumberOfInstallments() {
        return numberOfInstallments;
    }

    public void setNumberOfInstallments(final Integer numberOfInstallments) {
        this.numberOfInstallments = numberOfInstallments;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("InstallmentsPlan [");
        if (id != null) {
            builder.append("id=").append(id).append(", ");
        }
        if (schedule != null) {
            builder.append("schedule=").append(schedule).append(", ");
        }
        if (dueDays != null) {
            builder.append("dueDays=").append(dueDays).append(", ");
        }
        if (currency != null) {
            builder.append("currency=").append(currency).append(", ");
        }
        if (interestAmount != null) {
            builder.append("interestAmount=").append(interestAmount).append(", ");
        }
        if (installmentSetupFee != null) {
            builder.append("installmentSetupFee=").append(installmentSetupFee).append(", ");
        }
        if (installmentPeriodicFee != null) {
            builder.append("installmentPeriodicFee=").append(installmentPeriodicFee).append(", ");
        }
        if (installmentFee != null) {
            builder.append("installmentFee=").append(installmentFee).append(", ");
        }
        if (totalAmount != null) {
            builder.append("totalAmount=").append(totalAmount).append(", ");
        }
        if (nominalInterestRate != null) {
            builder.append("nominalInterestRate=").append(nominalInterestRate).append(", ");
        }
        if (effectiveInterestRate != null) {
            builder.append("effectiveInterestRate=").append(effectiveInterestRate).append(", ");
        }
        if (creditInformationUrl != null) {
            builder.append("creditInformationUrl=").append(creditInformationUrl).append(", ");
        }
        if (termsAndConditionsUrl != null) {
            builder.append("termsAndConditionsUrl=").append(termsAndConditionsUrl).append(", ");
        }
        if (dataPrivacyConsentUrl != null) {
            builder.append("dataPrivacyConsentUrl=").append(dataPrivacyConsentUrl).append(", ");
        }
        if (logoUrl != null) {
            builder.append("logoUrl=").append(logoUrl).append(", ");
        }
        if (description != null) {
            builder.append("description=").append(description).append(", ");
        }
        if (numberOfInstallments != null) {
            builder.append("numberOfInstallments=").append(numberOfInstallments);
        }
        builder.append("]");
        return builder.toString();
    }
}
