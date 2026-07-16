package com.trainday.microserviceDiet.api.DTO.response;

public record LoginNutriResponse(
        String id,
        String email,
        String crn,
        String Role
) {
}
