package com.medilexV2.medPlus.dto;

public class RecentlyRegisterMedicals {
    private String medicalName;
    private String email;
    private String medicalAddress;

    public RecentlyRegisterMedicals() {
    }

    public RecentlyRegisterMedicals(String medicalName, String email, String medicalAddress) {
        this.medicalName = medicalName;
        this.email = email;
        this.medicalAddress = medicalAddress;
    }

    public String getMedicalName() {
        return medicalName;
    }

    public void setMedicalName(String medicalName) {
        this.medicalName = medicalName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMedicalAddress() {
        return medicalAddress;
    }

    public void setMedicalAddress(String medicalAddress) {
        this.medicalAddress = medicalAddress;
    }
}
