package com.medilexV2.medPlus.service;

import com.medilexV2.medPlus.dto.RecentlyRegisterMedicals;
import com.medilexV2.medPlus.entity.Medical;
import com.medilexV2.medPlus.repository.MedicalRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private final MedicalRepository medicalRepository;

    public AdminService(MedicalRepository medicalRepository) {
        this.medicalRepository = medicalRepository;
    }

    public Integer getTotalMedicalStores(){
        return medicalRepository.findAll().size();
    }

    public Integer getTotalActiveMedicalStores(){
        return medicalRepository.findAll().stream()
                .filter(medical -> medical.getActive().equals(true))
                .toList()
                .size();
    }

    public List<RecentlyRegisterMedicals> getRecentlyRegisteredMedicals() {
        List<Medical> medicals = medicalRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        return medicals.stream()
                .map(medical -> new RecentlyRegisterMedicals(medical.getMedicalName(), medical.getEmail(), medical.getMedicalAddress()))
                .toList();
    }



}
