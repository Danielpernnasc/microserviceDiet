package com.trainday.microserviceDiet.application.service;

import com.trainday.microserviceDiet.Repository.LoginNutritionistRepository;
import com.trainday.microserviceDiet.Repository.NutritionistRepository;
import com.trainday.microserviceDiet.api.DTO.request.LoginRequest;
import com.trainday.microserviceDiet.api.DTO.request.RegisterNutritionistReq;
import com.trainday.microserviceDiet.infra.service.JwtService;
import com.trainday.microserviceDiet.models.LoginNutri;
import com.trainday.microserviceDiet.models.enums.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginNutritionistServiceTest {
    @Mock
    private JwtService jwtService;
    @Mock
    private LoginNutritionistRepository loginRepository;
    @Mock
    private NutritionistRepository nutritionistRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private LoginNutritionistService service;

    @Test
    void createLoginStoresEncodedPasswordAndReturnsLoginResponse() {
        RegisterNutritionistReq request = new RegisterNutritionistReq(
                "CRN-1", "ana@example.com", Role.NUTRITIONIST, "secret"
        );
        when(loginRepository.existsByCrn("CRN-1")).thenReturn(false);
        when(loginRepository.existsByEmail("ana@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret")).thenReturn("encoded-secret");
        when(loginRepository.save(any(LoginNutri.class))).thenAnswer(invocation -> {
            LoginNutri login = invocation.getArgument(0);
            login.setId("login-1");
            return login;
        });

        var response = service.createLogin(request);

        assertEquals("login-1", response.id());
        assertEquals("ana@example.com", response.email());
        assertEquals("CRN-1", response.crn());
        assertEquals(Role.NUTRITIONIST.getState(), response.Role());
        verify(passwordEncoder).encode("secret");
        verify(loginRepository).save(any(LoginNutri.class));
    }

    @Test
    void createLoginRejectsDuplicateCrnOrEmail() {
        RegisterNutritionistReq request = new RegisterNutritionistReq(
                "CRN-1", "ana@example.com", Role.NUTRITIONIST, "secret"
        );
        when(loginRepository.existsByCrn("CRN-1")).thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.createLogin(request)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verify(loginRepository, never()).save(any());
    }

    @Test
    void authenticateByCrnReturnsTokenForNutritionistWithoutProfile() {
        LoginNutri login = new LoginNutri();
        login.setId("login-1");
        login.setCrn("CRN-1");
        login.setEmail("ana@example.com");
        login.setPassword("encoded-secret");
        when(loginRepository.findByEmail("CRN-1")).thenReturn(Optional.empty());
        when(loginRepository.findByCrn("CRN-1")).thenReturn(Optional.of(login));
        when(passwordEncoder.matches("secret", "encoded-secret")).thenReturn(true);
        when(jwtService.generateToken("ana@example.com", "CRN-1", "login-1", Role.NUTRITIONIST))
                .thenReturn("jwt-token");

        String token = service.authenticate(new LoginRequest("CRN-1", "secret"));

        assertEquals("jwt-token", token);
    }

    @Test
    void authenticateRejectsUnknownLogin() {
        when(loginRepository.findByEmail("unknown")).thenReturn(Optional.empty());
        when(loginRepository.findByCrn("unknown")).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.authenticate(new LoginRequest("unknown", "secret"))
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    }

    @Test
    void authenticateRejectsIncorrectPassword() {
        LoginNutri login = new LoginNutri();
        login.setCrn("CRN-1");
        login.setPassword("encoded-secret");
        when(loginRepository.findByEmail("CRN-1")).thenReturn(Optional.empty());
        when(loginRepository.findByCrn("CRN-1")).thenReturn(Optional.of(login));
        when(passwordEncoder.matches("wrong", "encoded-secret")).thenReturn(false);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.authenticate(new LoginRequest("CRN-1", "wrong"))
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        verify(jwtService, never()).generateToken(any(), any(), any(), any());
    }
}
