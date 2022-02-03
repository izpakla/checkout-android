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
import java.util.Date;

/**
 * This class is designed to hold product information.
 */
public class Product {
    /** optional */
    private String code;
    /** mandatory */
    private String name;
    /** optional (totalAmount) */
    private BigDecimal amount;
    /** optional */
    private String currency;
    /** optional */
    private Integer quantity;
    /** optional */
    private Date plannedShippingDate;
    /** optional */
    private URL productDescriptionUrl;
    /** optional */
    private URL productImageUrl;
    /** optional */
    private String description;
    /** optional */
    private String shippingAddressId;
    /** optional */
    private ProductType type;
    /** optional */
    private BigDecimal netAmount;
    /** optional */
    private BigDecimal taxAmount;
    /** optional */
    private BigDecimal taxRatePercentage;

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(final BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(final String currency) {
        this.currency = currency;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(final Integer quantity) {
        this.quantity = quantity;
    }

    public Date getPlannedShippingDate() {
        return plannedShippingDate;
    }

    public void setPlannedShippingDate(final Date plannedShippingDate) {
        this.plannedShippingDate = plannedShippingDate;
    }

    public URL getProductDescriptionUrl() {
        return productDescriptionUrl;
    }

    public void setProductDescriptionUrl(final URL productDescriptionUrl) {
        this.productDescriptionUrl = productDescriptionUrl;
    }

    public URL getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(final URL productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getShippingAddressId() {
        return shippingAddressId;
    }

    public void setShippingAddressId(final String shippingAddressId) {
        this.shippingAddressId = shippingAddressId;
    }

    public ProductType getType() {
        return type;
    }

    public void setType(final ProductType type) {
        this.type = type;
    }

    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(final BigDecimal netAmount) {
        this.netAmount = netAmount;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(final BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getTaxRatePercentage() {
        return taxRatePercentage;
    }

    public void setTaxRatePercentage(final BigDecimal taxRatePercentage) {
        this.taxRatePercentage = taxRatePercentage;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Product [");
        if (code != null) {
            builder.append("code=").append(code).append(", ");
        }
        if (name != null) {
            builder.append("name=").append(name).append(", ");
        }
        if (amount != null) {
            builder.append("amount=").append(amount).append(", ");
        }
        if (currency != null) {
            builder.append("currency=").append(currency).append(", ");
        }
        if (quantity != null) {
            builder.append("quantity=").append(quantity).append(", ");
        }
        if (plannedShippingDate != null) {
            builder.append("plannedShippingDate=").append(plannedShippingDate).append(", ");
        }
        if (productDescriptionUrl != null) {
            builder.append("productDescriptionUrl=").append(productDescriptionUrl).append(", ");
        }
        if (productImageUrl != null) {
            builder.append("productImageUrl=").append(productImageUrl).append(", ");
        }
        if (description != null) {
            builder.append("description=").append(description).append(", ");
        }
        if (shippingAddressId != null) {
            builder.append("shippingAddressId=").append(shippingAddressId).append(", ");
        }
        if (type != null) {
            builder.append("type=").append(type).append(", ");
        }
        if (netAmount != null) {
            builder.append("netAmount=").append(netAmount).append(", ");
        }
        if (taxAmount != null) {
            builder.append("taxAmount=").append(taxAmount).append(", ");
        }
        if (taxRatePercentage != null) {
            builder.append("taxRatePercentage=").append(taxRatePercentage);
        }
        builder.append("]");
        return builder.toString();
    }
}
