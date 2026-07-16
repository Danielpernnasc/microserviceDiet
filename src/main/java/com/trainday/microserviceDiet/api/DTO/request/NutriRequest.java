package com.trainday.microserviceDiet.api.DTO.request;

import com.trainday.microserviceDiet.models.enums.Role;

import java.time.LocalDate;

public record NutriRequest(
         String name,
         LocalDate bornDate,
         String cpf,
         String phone,
         String address,
         Boolean active
) {
}
