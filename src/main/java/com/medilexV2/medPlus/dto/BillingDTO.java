package com.medilexV2.medPlus.dto;

import java.time.LocalDateTime;
import java.util.List;

public class BillingDTO {
    List<BillingInfo> billingInfoList;
    private String customerName;
    private Double totalAmount;


    public BillingDTO() {
    }

    public BillingDTO(List<BillingInfo> billingInfoList, String customerName, Double totalAmount) {
        this.billingInfoList = billingInfoList;
        this.customerName = customerName;
        this.totalAmount = totalAmount;

    }

    public List<BillingInfo> getBillingInfoList() {
        return billingInfoList;
    }

    public void setBillingInfoList(List<BillingInfo> billingInfoList) {
        this.billingInfoList = billingInfoList;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }
}
