package com.medilexV2.medPlus.service;

import com.medilexV2.medPlus.dto.NearbyMedicalProductDTO;
import com.medilexV2.medPlus.dto.Products;
import com.medilexV2.medPlus.entity.Medical;
import com.medilexV2.medPlus.entity.MedicalLocation;
import com.medilexV2.medPlus.exceptions.ResourceNotFoundException;
import com.medilexV2.medPlus.repository.MedicalLocationRepository;
import com.medilexV2.medPlus.repository.MedicalRepository;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public List<NearbyMedicalProductDTO> findNearbyMedicalsWithProduct(double latitude, double longitude, double maxDistance, String productName) {
        List<MedicalLocation> nearbyLocations = findNearbyLocations(longitude, latitude, maxDistance);
        System.out.println("Nearby Locations: " + nearbyLocations);

        List<NearbyMedicalProductDTO> nearbyMedicalsWithProduct = new ArrayList<>();
        for (MedicalLocation location : nearbyLocations) {
            Medical medical = medicalRepository.findByEmail(location.getEmail()).orElseThrow(() -> new ResourceNotFoundException("Medical not found with email: " + location.getEmail()));
            boolean match = medical.getProducts().stream()
                    .anyMatch(products -> products.getProductName().equalsIgnoreCase(productName));
            if(match){
                NearbyMedicalProductDTO dto = new NearbyMedicalProductDTO();
                Products first = medical.getProducts().stream().filter(products -> products.getProductName().equalsIgnoreCase(productName)).findFirst().get();
                dto.setProductName(first.getProductName());
                dto.setMrp(first.getMrp());
                dto.setMedicalName(medical.getMedicalName());
                nearbyMedicalsWithProduct.add(dto);
            }
        }
        return nearbyMedicalsWithProduct;

    }

    private Medical getCurrentuser(){
        return (Medical) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public List<MedicalLocation> findAllLocations() {
        return medicalLocationRepository.findAll();
    }
}
