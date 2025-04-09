package com.medilexV2.medPlus.repository;

import com.medilexV2.medPlus.entity.Medical;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MedicalRepository extends MongoRepository<Medical, String>,MedicalRepositoryCustom {
    Optional<Medical> findByEmail(String email);
}