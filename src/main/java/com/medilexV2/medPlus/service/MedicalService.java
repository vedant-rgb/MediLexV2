package com.medilexV2.medPlus.service;

import com.medilexV2.medPlus.entity.Medical;
import com.medilexV2.medPlus.repository.MedicalRepository;
import org.springframework.stereotype.Service;

@Service
public class MedicalService {

    private final MedicalRepository medicalRepository;

    public MedicalService(MedicalRepository medicalRepository) {
        this.medicalRepository = medicalRepository;
    }



}
