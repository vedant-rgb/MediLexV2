package com.medilexV2.medPlus.dto;

import com.fasterxml.jackson.annotation.JsonProperty;


public class Products {
    @JsonProperty("hsn")
    private String hsn;

    @JsonProperty("productName")
    private String productName;

    @JsonProperty("mfg")
    private String mfg;

    @JsonProperty("unit")
    private String unit;

    @JsonProperty("qty")
    private Integer qty;

    @JsonProperty("currentStock")
    private Integer currentStock;

    @JsonProperty("sch")
    private Integer sch;

    @JsonProperty("batch")
    private String batch;

    @JsonProperty("exp")
    private String exp;

    @JsonProperty("mrp")
    private Double mrp;

    @JsonProperty("rate")
    private Double rate;

    @JsonProperty("ptr")
    private Double ptr;

    @JsonProperty("gst")
    private Double gst;

    @JsonProperty("amount")
    private Double amount;



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

    public String getMfg() {
        return mfg;
    }

    public void setMfg(String mfg) {
        this.mfg = mfg;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public Integer getSch() {
        return sch;
    }

    public void setSch(Integer sch) {
        this.sch = sch;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }

    public Double getMrp() {
        return mrp;
    }

    public void setMrp(Double mrp) {
        this.mrp = mrp;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Double getPtr() {
        return ptr;
    }

    public void setPtr(Double ptr) {
        this.ptr = ptr;
    }

    public Double getGst() {
        return gst;
    }

    public void setGst(Double gst) {
        this.gst = gst;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Integer getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(Integer currentStock) {
        this.currentStock = currentStock;
    }




    @Override
    public String toString() {
        return "Products{" +
                "hsn='" + hsn + '\'' +
                ", productName='" + productName + '\'' +
                ", mfg='" + mfg + '\'' +
                ", unit='" + unit + '\'' +
                ", qty=" + qty +
                ", sch=" + sch +
                ", batch='" + batch + '\'' +
                ", exp='" + exp + '\'' +
                ", mrp=" + mrp +
                ", rate=" + rate +
                ", ptr=" + ptr +
                ", gst=" + gst +
                ", amount=" + amount +
                '}';
    }
}