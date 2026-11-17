package com.trainday.microserviceDiet.infra.Security;

import com.trainday.microserviceDiet.models.LoginNutri;
import com.trainday.microserviceDiet.models.enums.Role;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserDetailImplTest {

    @Test
    void exposesEmailAsUsernameAndLoginPassword() {
        LoginNutri login = new LoginNutri(
                "id", "12345", "nutritionist@example.com", "hashed-password", Role.NUTRITIONIST);
        UserDetailImpl userDetails = new UserDetailImpl(login);

        assertEquals("nutritionist@example.com", userDetails.getUsername());
        assertEquals("hashed-password", userDetails.getPassword());
    }

    @Test
    void hasNoAuthorities() {
        LoginNutri login = new LoginNutri(
                "id", "12345", "nutritionist@example.com", "hashed-password", Role.NUTRITIONIST);
        UserDetailImpl userDetails = new UserDetailImpl(login);

        assertTrue(userDetails.getAuthorities().isEmpty());
    }

    @Test
    void accountAndCredentialsAreAlwaysValidAndEnabled() {
        LoginNutri login = new LoginNutri(
                "id", "12345", "nutritionist@example.com", "hashed-password", Role.NUTRITIONIST);
        UserDetailImpl userDetails = new UserDetailImpl(login);

        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isEnabled());
    }
}
