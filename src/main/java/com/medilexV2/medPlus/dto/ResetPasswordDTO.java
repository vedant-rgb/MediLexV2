package com.medilexV2.medPlus.dto;

import lombok.Data;

public class ResetPasswordDTO {
    private String oldPassword;
    private String newPassword;

    public ResetPasswordDTO() {
    }

    public ResetPasswordDTO(String oldPassword, String newPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
