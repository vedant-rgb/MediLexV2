package com.medilexV2.medPlus.dto;

import com.medilexV2.medPlus.entity.Medical;

// MedicalDistanceDto.java
public class MedicalDistanceDto {
    private Medical medical;
    private double distance;
    private String productName;

    public MedicalDistanceDto(Medical medical, double distance, String productName) {
        this.medical = medical;
        this.distance = distance;
        this.productName = productName;
    }

    // Getters and Setters

    public Medical getMedical() {
        return medical;
    }

    public void setMedical(Medical medical) {
        this.medical = medical;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
