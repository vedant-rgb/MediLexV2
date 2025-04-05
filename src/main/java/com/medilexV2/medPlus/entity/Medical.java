package com.medilexV2.medPlus.entity;

import com.medilexV2.medPlus.dto.Products;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collation = "MedicalStore")
public class Medical {
    @Id
    private String id;

    private String email;

    private String medicalName;
    private String medicalAddress;
    private String licenseNumber;
    private String contactNumber;


    private GeoJsonPoint location;
    private List<String> photos;
    private List<Products> products;

    public Medical() {
    }

    public Medical(String id, String email, String medicalName, String medicalAddress, String licenseNumber, String contactNumber, GeoJsonPoint location, List<String> photos, List<Products> products) {
        this.id = id;
        this.email = email;
        this.medicalName = medicalName;
        this.medicalAddress = medicalAddress;
        this.licenseNumber = licenseNumber;
        this.contactNumber = contactNumber;
        this.location = location;
        this.photos = photos;
        this.products = products;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMedicalName() {
        return medicalName;
    }

    public void setMedicalName(String medicalName) {
        this.medicalName = medicalName;
    }

    public String getMedicalAddress() {
        return medicalAddress;
    }

    public void setMedicalAddress(String medicalAddress) {
        this.medicalAddress = medicalAddress;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
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

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public List<Products> getProducts() {
        return products;
    }

    public void setProducts(List<Products> products) {
        this.products = products;
    }

    @Override
    public String toString() {
        return "Medical{" +
                "email='" + email + '\'' +
                ", medicalName='" + medicalName + '\'' +
                ", medicalAddress='" + medicalAddress + '\'' +
                ", licenseNumber='" + licenseNumber + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", location=" + location +
                ", photos=" + photos +
                ", products=" + products +
                '}';
    }
}
