package com.trainday.microserviceDiet.infra.Security;

import com.trainday.microserviceDiet.models.LoginNutri;
import com.trainday.microserviceDiet.models.enums.Role;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserDetailImplTest {

    @Test
    void preservesLoginAfterSerialization() throws Exception {
        LoginNutri login = new LoginNutri(
                "id", "12345", "nutritionist@example.com", "hashed-password", Role.NUTRITIONIST);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream output = new ObjectOutputStream(bytes)) {
            output.writeObject(new UserDetailImpl(login));
        }

        try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            UserDetailImpl restored = (UserDetailImpl) input.readObject();
            assertEquals(login.getEmail(), restored.getUsername());
            assertEquals(login.getPassword(), restored.getPassword());
            assertTrue(restored.getAuthorities().isEmpty());
        }
    }

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
