package com.medilexV2.medPlus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public class NearbyMedicalProductDTO {
    private String medicalName;
    private String productName;
    private Double mrp;


    public NearbyMedicalProductDTO() {
    }

    public NearbyMedicalProductDTO(String medicalName, String productName, Double mrp) {
        this.medicalName = medicalName;
        this.productName = productName;
        this.mrp = mrp;
    }

    public String getMedicalName() {
        return medicalName;
    }

    public void setMedicalName(String medicalName) {
        this.medicalName = medicalName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Double getMrp() {
        return mrp;
    }

    public void setMrp(Double mrp) {
        this.mrp = mrp;
    }
}
