package com.trainday.microserviceDiet.api.DTO.response;

import com.trainday.microserviceDiet.models.enums.Role;

public record NutricionistResponse(
        String id,
        String name,
        String email,
        String crn,
        Role ROLE
) {

}
