package com.medilexV2.medPlus.controller;

import com.medilexV2.medPlus.entity.MedicalLocation;
import com.medilexV2.medPlus.repository.MedicalRepository;
import com.medilexV2.medPlus.service.MedicalLocationService;
import org.bson.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medical-location")
public class MedicalLocationController {

    private final MedicalLocationService medicalLocationService;
    private final MedicalRepository medicalRepository;

    public MedicalLocationController(MedicalLocationService medicalLocationService, MedicalRepository medicalRepository) {
        this.medicalLocationService = medicalLocationService;
        this.medicalRepository = medicalRepository;
    }

    @PostMapping("/save")
    public String saveLocation(@RequestParam double longitude,@RequestParam double latitude) {
        return medicalLocationService.saveLocation(longitude, latitude);
    }

    @GetMapping("/nearby")
    public List<MedicalLocation> findNearbyLocations(@RequestParam double longitude, @RequestParam double latitude, @RequestParam(defaultValue = "2000") double maxDistance) {
        return medicalLocationService.findNearbyLocations(longitude, latitude, maxDistance);
    }

    @GetMapping("/findAll")
    public List<MedicalLocation> findAllLocations() {
        return medicalLocationService.findAllLocations();
    }

}