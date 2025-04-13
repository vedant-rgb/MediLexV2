package com.medilexV2.medPlus.entity;

import com.medilexV2.medPlus.dto.Products;
import com.medilexV2.medPlus.dto.RecentOrders;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Document(collection = "MedicalStore")
public class Medical implements UserDetails {
    @Id
    private String id;

    private String email;
    private String password;
    private String medicalName;
    private String medicalAddress;
    private String licenseNumber;
    private String contactNumber;
    private Boolean firstTimeLogin;
    private List<String> photos;
    private List<Products> products;
    private Boolean active;
    private List<RecentOrders> recentOrders;
    private String role;
    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public Medical() {
    }

    public Medical(String id, String email, String password, String medicalName, String medicalAddress, String licenseNumber, String contactNumber, Boolean firstTimeLogin, List<String> photos, List<Products> products, Boolean active, List<RecentOrders> recentOrders, String role, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.medicalName = medicalName;
        this.medicalAddress = medicalAddress;
        this.licenseNumber = licenseNumber;
        this.contactNumber = contactNumber;
        this.firstTimeLogin = firstTimeLogin;
        this.photos = photos;
        this.products = products;
        this.active = active;
        this.recentOrders = recentOrders;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Boolean getFirstTimeLogin() {
        return firstTimeLogin;
    }

    public void setFirstTimeLogin(Boolean firstTimeLogin) {
        this.firstTimeLogin = firstTimeLogin;
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

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<RecentOrders> getRecentOrders() {
        return recentOrders;
    }

    public void setRecentOrders(List<RecentOrders> recentOrders) {
        this.recentOrders = recentOrders;
    }

    @Override
    public String toString() {
        return "Medical{" +
                "email='" + email + '\'' +
                ", medicalName='" + medicalName + '\'' +
                ", medicalAddress='" + medicalAddress + '\'' +
                ", licenseNumber='" + licenseNumber + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", photos=" + photos +
                ", products=" + products +
                '}';
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }


    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

}
