package com.trainday.microserviceDiet.infra.client;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "athlete-service", url = "http://localhost:8080")
public interface AthleteClient {
    @GetMapping("/athlete/internal/cpf/{cpf}")
    Object findByCpf(@PathVariable String cpf);
}
