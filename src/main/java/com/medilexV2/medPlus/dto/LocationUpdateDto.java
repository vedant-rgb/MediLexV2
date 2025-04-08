package com.medilexV2.medPlus.dto;

public class LocationUpdateDto {
    private double longitude;
    private double latitude;

    // Constructors
    public LocationUpdateDto() {
    }

    public LocationUpdateDto(String medicalId, double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    // Getters and Setters
    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}