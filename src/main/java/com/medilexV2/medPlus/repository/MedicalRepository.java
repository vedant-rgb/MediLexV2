package com.medilexV2.medPlus.repository;

import com.medilexV2.medPlus.entity.Medical;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicalRepository extends MongoRepository<Medical, String> {
    Optional<Medical> findByEmail(String email);

    // Find medical stores near a location within a certain distance
    List<Medical> findByLocationNear(Point location, Distance distance);

    // Or use a custom query with more control
    @Query("{ 'location' : { $near : { $geometry : { type : 'Point', coordinates : [?0, ?1] } } } }")
    List<Medical> findNearbyMedicalStores(double longitude, double latitude);
}
