package com.medilexV2.medPlus.controller;

import com.medilexV2.medPlus.dto.RecentlyRegisterMedicals;
import com.medilexV2.medPlus.dto.SignUpRequest;
import com.medilexV2.medPlus.entity.Medical;
import com.medilexV2.medPlus.security.AuthService;
import com.medilexV2.medPlus.service.AdminService;
import com.medilexV2.medPlus.service.MedicalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final AuthService authService;
    private final MedicalService medicalService;

    public AdminController(AdminService adminService, AuthService authService, MedicalService medicalService) {
        this.adminService = adminService;
        this.authService = authService;
        this.medicalService = medicalService;
    }

    @GetMapping("/totalMedicalStores")
    public ResponseEntity<Integer> getTotalMedicals(){
        return ResponseEntity.ok(adminService.getTotalMedicalStores());
    }

    @GetMapping("/totalActiveMedicalStores")
    public ResponseEntity<Integer> getTotalActiveMedicals(){
        return ResponseEntity.ok(adminService.getTotalActiveMedicalStores());
    }

    @PostMapping("/addNewStore")
    public ResponseEntity<String> createNewTeacher(@RequestBody SignUpRequest signUpRequest){
        return new ResponseEntity<>(authService.signUp(signUpRequest), HttpStatus.CREATED);
    }

    @GetMapping("/recentlyRegisterMedicalStores")
    public ResponseEntity<List<RecentlyRegisterMedicals>> getRecentlyRegisterMedicalStores(){
        return ResponseEntity.ok(adminService.getRecentlyRegisteredMedicals());
    }

    @GetMapping("/getAllMedical")
    public ResponseEntity<?> getAllMedical() {
        List<Medical> medicals = medicalService.getAllMedical();
        return ResponseEntity.ok(medicals);
    }






}
