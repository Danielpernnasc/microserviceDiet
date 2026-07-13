package com.trainday.microserviceDiet.Repository;

import com.trainday.microserviceDiet.models.LoginNutri;
import com.trainday.microserviceDiet.models.Nutricionist;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface NutricionistRepository extends MongoRepository<Nutricionist, String> {

    Optional<Nutricionist> findByEmail(String email);

    Optional<Nutricionist> findByCrn(String crn);
}
