package com.medilexV2.medPlus.dto;

public class AllProducts {
    private String productName;
    private Integer qty;

    public AllProducts() {
    }

    public AllProducts(String productName, Integer qty) {
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
