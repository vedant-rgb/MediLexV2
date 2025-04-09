package com.medilexV2.medPlus.repository;

import org.bson.Document;
import java.util.List;

public interface MedicalRepositoryCustom {
    List<Document> findNearbyMedicalsWithMedicine(String medicineName, double lat, double lng, double radiusInMeters);
}
