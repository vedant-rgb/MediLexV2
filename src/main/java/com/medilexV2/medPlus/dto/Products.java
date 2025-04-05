package com.medilexV2.medPlus.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.processing.Generated;
import java.util.Objects;

@RequiredArgsConstructor
@Document(collection = "products")
public class Products {
    @JsonProperty("HSN")
    private String hsn;

    @JsonProperty("Product Name")
    private String productName;

    @JsonProperty("Mfg")
    private String mfg;

    @JsonProperty("Unit")
    private String unit;

    @JsonProperty("Qty")
    private Integer qty;

    @JsonProperty("Sch")
    private Integer sch;

    @JsonProperty("Batch")
    private String batch;

    @JsonProperty("Exp")
    private String exp;

    @JsonProperty("MRP")
    private Double mrp;

    @JsonProperty("Rate")
    private Double rate;

    @JsonProperty("PTR")
    private Double ptr;

    @JsonProperty("Gst")
    private Double gst;

    @JsonProperty("Amount")
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