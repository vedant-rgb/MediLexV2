package com.medilexV2.medPlus.dto;

public class BillingProducts {
    private String hsn;
    private String productName;
    private String currentStock;
    private Double mrp;

    public BillingProducts() {
    }

    public BillingProducts(String hsn, String productName, String currentStock, Double mrp) {
        this.hsn = hsn;
        this.productName = productName;
        this.currentStock = currentStock;
        this.mrp = mrp;
    }

    public String getHsn() {
        return hsn;
    }

    public void setHsn(String hsn) {
        this.hsn = hsn;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(String currentStock) {
        this.currentStock = currentStock;
    }

    public Double getMrp() {
        return mrp;
    }

    public void setMrp(Double mrp) {
        this.mrp = mrp;
    }
}
