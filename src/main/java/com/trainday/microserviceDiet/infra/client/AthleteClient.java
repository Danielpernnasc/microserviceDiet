package com.trainday.microserviceDiet.infra.client;


import com.trainday.microserviceDiet.infra.DTO.response.AthleteCientResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "athlete-service", url = "http://localhost:8080")
public interface AthleteClient {

    @GetMapping("/athlete/internal/cpf/{cpf}")
    AthleteCientResponse findByCpf(
            @PathVariable("cpf") String cpf
    );
}