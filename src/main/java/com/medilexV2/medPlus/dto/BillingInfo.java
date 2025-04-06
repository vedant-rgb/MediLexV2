package com.medilexV2.medPlus.dto;

public class BillingInfo {
    private String productName;
    private Integer qty;

    public BillingInfo() {
    }

    public BillingInfo(String productName, Integer qty) {
        this.productName = productName;
        this.qty = qty;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }
}
