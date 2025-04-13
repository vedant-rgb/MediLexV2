package com.medilexV2.medPlus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;


public class ExpiringProductDTO {
    private String medicalName;
    private Products product;


    public ExpiringProductDTO(String medicalName, Products product) {
        this.medicalName = medicalName;
        this.product = product;
    }
    public String getMedicalName() {
        return medicalName;
    }
    public void setMedicalName(String medicalName) {
        this.medicalName = medicalName;
    }
    public Products getProduct() {
        return product;
    }
    public void setProduct(Products product) {
        this.product = product;
    }
}
