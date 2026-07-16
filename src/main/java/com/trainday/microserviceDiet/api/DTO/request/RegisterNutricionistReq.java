package com.trainday.microserviceDiet.api.DTO.request;

import com.trainday.microserviceDiet.models.enums.Role;

import java.time.LocalDate;

public record RegisterNutricionistReq(

        String crn,
        String email,
        Role role,
        String password
) {
}
