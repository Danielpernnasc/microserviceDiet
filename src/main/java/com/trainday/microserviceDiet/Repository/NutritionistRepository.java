package com.trainday.microserviceDiet.Repository;

import com.trainday.microserviceDiet.models.Nutritionist;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface NutritionistRepository extends MongoRepository<Nutritionist, String> {

    Optional<Nutritionist> findByEmail(String email);

    Optional<Nutritionist> findByCrn(String crn);

}
