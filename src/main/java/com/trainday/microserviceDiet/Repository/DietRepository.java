package com.trainday.microserviceDiet.Repository;

import com.trainday.microserviceDiet.models.Diet;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface DietRepository extends MongoRepository<Diet, String> {
    List<Diet> findByAthleteCpf(String athleteCpf);

    Optional<Diet> findFirstByAthleteCpf(String athleteCpf);
}
