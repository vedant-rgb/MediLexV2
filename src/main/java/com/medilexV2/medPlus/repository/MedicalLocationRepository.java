package com.medilexV2.medPlus.repository;

import com.medilexV2.medPlus.entity.MedicalLocation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MedicalLocationRepository extends MongoRepository<MedicalLocation, String>{

     Optional<MedicalLocation> findByEmail(String email);

     @Query("{ 'location' : { $near : { $geometry : { type : 'Point', coordinates : [?0, ?1] }, $maxDistance : ?2 } } }")
     List<MedicalLocation> findNearby(double longitude, double latitude, double maxDistance);
}
