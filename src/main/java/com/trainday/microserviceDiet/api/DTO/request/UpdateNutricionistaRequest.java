package com.trainday.microserviceDiet.api.DTO.request;

import com.trainday.microserviceDiet.models.enums.Role;

import java.time.LocalDate;

public record UpdateNutricionistaRequest(
        String name,
        LocalDate bornDate,
        String cpf,
        String phone,
        String email,
        String address,
        Boolean active
) {
}
