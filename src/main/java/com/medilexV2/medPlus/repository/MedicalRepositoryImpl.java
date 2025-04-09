package com.medilexV2.medPlus.repository;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.core.query.Criteria;


import java.util.List;

@Repository
public class MedicalRepositoryImpl implements MedicalRepositoryCustom {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<Document> findNearbyMedicalsWithMedicine(String medicineName, double lat, double lng, double radiusInMeters) {
        AggregationOperation geoNear = Aggregation.geoNear(
                NearQuery.near(lng, lat)
                        .maxDistance(radiusInMeters / 1000.0)
                        .spherical(true),
                "distance"
        );

        Criteria matchCriteria = Criteria.where("products")
                .elemMatch(Criteria.where("productName").regex("^" + medicineName + "$", "i"));

        AggregationOperation matchStage = Aggregation.match(matchCriteria);

        AggregationOperation projectStage = context -> new Document("$project", new Document()
                .append("medicalName", 1)
                .append("medicalAddress", 1)
                .append("contactNumber", 1)
                .append("distance", 1)
                .append("products", new Document("$filter", new Document()
                        .append("input", "$products")
                        .append("as", "product")
                        .append("cond", new Document("$eq", List.of("$$product.productName", medicineName)))
                ))
        );

        Aggregation aggregation = Aggregation.newAggregation(
                geoNear,
                matchStage,
                projectStage
        );

        return mongoTemplate.aggregate(aggregation, "MedicalStore", Document.class).getMappedResults();
    }
}
