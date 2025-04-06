package com.medilexV2.medPlus.dto;

import java.util.List;

public class BillingDTO {
    List<BillingInfo> billingInfoList;

    public BillingDTO() {
    }

    public BillingDTO(List<BillingInfo> billingInfoList) {
        this.billingInfoList = billingInfoList;
    }

    public List<BillingInfo> getBillingInfoList() {
        return billingInfoList;
    }

    public void setBillingInfoList(List<BillingInfo> billingInfoList) {
        this.billingInfoList = billingInfoList;
    }
}
