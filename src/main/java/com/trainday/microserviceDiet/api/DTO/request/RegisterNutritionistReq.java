package com.trainday.microserviceDiet.api.DTO.request;

import com.trainday.microserviceDiet.models.enums.Role;

public record RegisterNutritionistReq(

        String crn,
        String email,
        Role role,
        String password
) {
}
