package com.trainday.microserviceDiet.Repository;

import com.trainday.microserviceDiet.models.LoginNutri;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface LoginNutricionistRepository extends MongoRepository<LoginNutri, String> {
    Optional<LoginNutri> findByCrn(String crn);

    Optional<LoginNutri> findByEmail(String email);

    boolean existsByCrn(String crn);

    boolean existsByEmail(String email);
}
