package com.medilexV2.medPlus.repository;

import com.medilexV2.medPlus.entity.Medical;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MedicalRepository extends MongoRepository<Medical, String> {
    Optional<Medical> findByEmail(String email);
}
