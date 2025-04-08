package com.medilexV2.medPlus.dto;

import com.medilexV2.medPlus.entity.Medical;

// MedicalWithDistance.java
public class MedicalWithDistance {
    private Medical medical;
    private double distance;

    public MedicalWithDistance(Medical medical, double distance) {
        this.medical = medical;
        this.distance = distance;
    }

    // Getters
    public Medical getMedical() {
        return medical;
    }

    public double getDistance() {
        return distance;
    }
}