package com.medilexV2.medPlus.controller;

import com.medilexV2.medPlus.dto.NearbyMedicalDTO;
import com.medilexV2.medPlus.dto.NearbyMedicalProductDTO;
import com.medilexV2.medPlus.entity.MedicalLocation;
import com.medilexV2.medPlus.service.MedicalLocationService;
import com.medilexV2.medPlus.service.MedicalService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medical-location")
public class MedicalLocationController {

    private final MedicalLocationService medicalLocationService;
    private final MedicalService medicalService;
    private static final Logger logger = LoggerFactory.getLogger(MedicalController.class);


    public MedicalLocationController(MedicalLocationService medicalLocationService, MedicalService medicalService) {
        this.medicalLocationService = medicalLocationService;
        this.medicalService = medicalService;
    }

    @PostMapping("/save")
    public String saveLocation(@RequestParam double longitude,@RequestParam double latitude) {
        return medicalLocationService.saveLocation(longitude, latitude);
    }

    @GetMapping("/nearby")
    public List<NearbyMedicalDTO> findNearbyLocations(@RequestParam double longitude, @RequestParam double latitude, @RequestParam(defaultValue = "2000") double maxDistance) {
        return medicalLocationService.findNearbyLocationsWithCoordinates(longitude, latitude, maxDistance);
    }

    @GetMapping("/findAll")
    public List<MedicalLocation> findAllLocations() {
        return medicalLocationService.findAllLocations();
    }

    @GetMapping("/nearby-products")
    public ResponseEntity<List<NearbyMedicalProductDTO>> findNearbyMedicalsWithProduct(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "2000") double maxDistance,
            @RequestParam String productName
    ) {
        logger.info("Received request to find nearby medicals with product '{}' at lat={}, lon={}, maxDistance={}",
                productName, latitude, longitude, maxDistance);

        List<NearbyMedicalProductDTO> nearby = medicalLocationService.findNearbyMedicalsWithProduct(latitude, longitude, maxDistance, productName);

        logger.info("Found {} nearby medical(s) with the product '{}'", nearby.size(), productName);
        return ResponseEntity.ok(nearby);
    }


}
