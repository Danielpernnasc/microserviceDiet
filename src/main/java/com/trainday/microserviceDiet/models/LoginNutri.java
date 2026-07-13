package com.trainday.microserviceDiet.models;

import com.trainday.microserviceDiet.models.enums.Role;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "login_nutricionist")
public class LoginNutri {
    @Id
    private String id;
    @Indexed(unique = true)
    private String crn;
    @Indexed(unique = true)
    private String email;
    private String password;
    @NotNull(message = "Role cannot be null")
    private Role role;
}
