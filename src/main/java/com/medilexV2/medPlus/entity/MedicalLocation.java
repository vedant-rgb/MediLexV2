package com.medilexV2.medPlus.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("medical_location")
public class MedicalLocation {
    @Id
    private String id;

    private String email;

    @GeoSpatialIndexed
    private GeoJsonPoint location;

    @Transient
    private double distance;

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public GeoJsonPoint getLocation() {
        return location;
    }

    public void setLocation(GeoJsonPoint location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "MedicalLocation{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", location=" + location +
                ", distance=" + distance +
                '}';
    }
}
