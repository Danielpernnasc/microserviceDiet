package com.trainday.microserviceDiet.infra.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "train-service", url = "http://localhost:8081")
public interface AthleteTrainClient {
    @GetMapping("/train/internal/cpf/{cpf}")
    Object findByCpf(@PathVariable String cpf);
}
