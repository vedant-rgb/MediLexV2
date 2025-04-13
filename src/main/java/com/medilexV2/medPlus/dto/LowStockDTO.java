package com.medilexV2.medPlus.dto;

public class LowStockDTO {
    private String productName;
    private Integer currentStockQty;
    private Integer originalStocksQty;

    public LowStockDTO() {
    }

    public LowStockDTO(String productName, Integer currentStockQty, Integer originalStocksQty) {
        this.productName = productName;
        this.currentStockQty = currentStockQty;
        this.originalStocksQty = originalStocksQty;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getCurrentStockQty() {
        return currentStockQty;
    }

    public void setCurrentStockQty(Integer currentStockQty) {
        this.currentStockQty = currentStockQty;
    }

    public Integer getOriginalStocksQty() {
        return originalStocksQty;
    }

    public void setOriginalStocksQty(Integer originalStocksQty) {
        this.originalStocksQty = originalStocksQty;
    }

}
