package com.trainday.microserviceDiet.infra.Service;

import com.trainday.microserviceDiet.infra.service.JwtService;
import com.trainday.microserviceDiet.models.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class JwtServiceTest {
    @InjectMocks
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        ReflectionTestUtils.setField(jwtService, "secret", "fake-secret-key-for-testing-trainday-2026!!");
        ReflectionTestUtils.setField(jwtService, "expiration", 3600000L);
    }

    @Test
    void shouldReturnedKey() {
        Key key = jwtService.testgetKey();
        assertNotNull(key);
    }

    @Test
    void shouldGenerateToken() {
        String token = jwtService.generateToken(
                "nutritionist@host.com.br", "CRN3-123456", "nutritionist", Role.NUTRITIONIST);

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void shouldReturnTrueWhenTokenIsValid() {
        String token = jwtService.generateToken("nutritionist@host.com.br", "CRN3-123456", "nutritionist", Role.NUTRITIONIST);
        assertTrue(jwtService.isTokenValid(token));
    }

    @Test
    void shouldReturnFalseWhenTokenIsInvalid() {
        assertFalse(jwtService.isTokenValid("token-invalido"));
    }

    @Test
    void shouldExtractUsername() {
        String token = jwtService.generateToken("nutritionist@host.com.br", "CRN3-123456", "nutritionist", Role.NUTRITIONIST);
        String username = jwtService.extractSubject(token);
        assertEquals("CRN3-123456", username);
    }
}
