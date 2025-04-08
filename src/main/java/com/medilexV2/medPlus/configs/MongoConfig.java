package com.medilexV2.medPlus.configs;

import com.medilexV2.medPlus.entity.Medical;
import jakarta.annotation.PostConstruct;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeospatialIndex;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;

@Configuration
public class MongoConfig {

    @Bean
    public String ensureGeospatialIndex(MongoTemplate mongoTemplate) {
        try {
            mongoTemplate.indexOps(Medical.class)
                    .ensureIndex(new GeospatialIndex("location"));
            return "Geospatial index created successfully";
        } catch (Exception e) {
            return "Failed to create geospatial index: " + e.getMessage();
        }
    }
}