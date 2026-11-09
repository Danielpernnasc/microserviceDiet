package com.trainday.microserviceDiet.infra.DTO.response;

import com.trainday.microserviceDiet.models.enums.Role;

public record AthleteCientResponse(
        String id,
        String cpf,
        String name,
        String socialName,
        String email,
        String born,
        String gender,
        String identity,
        String height,
        String weight,
        Role role
) {
}
