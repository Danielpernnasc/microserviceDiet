package com.trainday.microserviceDiet.Repository;

import com.trainday.microserviceDiet.models.Diet;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DietTemplateRepository extends MongoRepository<Diet, String> {
}
