package com.medilexV2.medPlus.service;

import com.medilexV2.medPlus.entity.Medical;
import com.medilexV2.medPlus.entity.MedicalLocation;
import com.medilexV2.medPlus.exceptions.ResourceNotFoundException;
import com.medilexV2.medPlus.repository.MedicalLocationRepository;
import com.medilexV2.medPlus.repository.MedicalRepository;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicalLocationService {

    private final MedicalLocationRepository medicalLocationRepository;
    private final MedicalRepository medicalRepository;

    public MedicalLocationService(MedicalLocationRepository medicalLocationRepository, MedicalRepository medicalRepository) {
        this.medicalLocationRepository = medicalLocationRepository;
        this.medicalRepository = medicalRepository;
    }

    public String saveLocation(double longitude, double latitude) {
        // Create a new MedicalLocation object
        String email = getCurrentuser().getEmail();
        if(!medicalRepository.findByEmail(email).isPresent()){
            throw new ResourceNotFoundException("Medical not found with email: " + email);
        }
        MedicalLocation medicalLocation = new MedicalLocation();
        medicalLocation.setEmail(email);
        medicalLocation.setLocation(new GeoJsonPoint(longitude, latitude));

        // Save the location to the database
        medicalLocationRepository.save(medicalLocation);
        return "Location saved successfully";
    }

    public List<MedicalLocation> findNearbyLocations(double longitude, double latitude, double maxDistance) {
        return medicalLocationRepository.findNearby(longitude, latitude, maxDistance);
    }

    private Medical getCurrentuser(){
        return (Medical) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public List<MedicalLocation> findAllLocations() {
        return medicalLocationRepository.findAll();
    }
}
