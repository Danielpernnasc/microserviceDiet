package com.trainday.microserviceDiet.models;


import com.trainday.microserviceDiet.models.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Nutricionist {
    private String id;
    private String name;
    private String email;
    private LocalDate bornDate;
    private String cpf;
    private String phone;
    private String address;
    private String crn;
    private Role role;
    private boolean active = true;
}
