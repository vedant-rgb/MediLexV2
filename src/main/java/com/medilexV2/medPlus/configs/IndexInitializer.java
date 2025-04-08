package com.medilexV2.medPlus.configs;

import com.medilexV2.medPlus.entity.Medical;
import jakarta.annotation.PostConstruct;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeospatialIndex;
import org.springframework.stereotype.Component;


@Component
public class IndexInitializer {

    private final MongoTemplate mongoTemplate;

    public IndexInitializer(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @PostConstruct
    public void initIndexes() {
        mongoTemplate.indexOps(Medical.class)
                .ensureIndex(new GeospatialIndex("location").typed(GeoSpatialIndexType.GEO_2DSPHERE));
    }
}
