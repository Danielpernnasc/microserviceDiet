package com.trainday.microserviceDiet.api.DTO.request;

import com.trainday.microserviceDiet.models.enums.Role;

import java.time.LocalDate;

public record RegisterNutricionistReq(
        String name,
        LocalDate bornDate,
        String crn,
        String cpf,
        String email,
        String phone,
        String address,
        Role role,
        String password
) {
}
