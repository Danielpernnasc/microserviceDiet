package com.trainday.microserviceDiet.api.controller;

import com.trainday.microserviceDiet.api.Controller.AuthController;
import com.trainday.microserviceDiet.api.DTO.request.LoginRequest;
import com.trainday.microserviceDiet.api.DTO.request.RegisterNutritionistReq;
import com.trainday.microserviceDiet.api.DTO.response.LoginNutriResponse;
import com.trainday.microserviceDiet.application.service.LoginNutritionistService;
import com.trainday.microserviceDiet.models.enums.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.bson.assertions.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {
    @Mock
    LoginNutritionistService service;

    @InjectMocks
    AuthController authController;

    @Test
    void shouldRegister(){
        RegisterNutritionistReq request = new RegisterNutritionistReq(
                "Ana", "nutritionist@professional.com.br", Role.NUTRITIONIST, "crn12345"
        );

        LoginNutriResponse response  = new LoginNutriResponse(
                "login-1",
                "nutritionist@professional.com.br",
                "CRN3-123456",
                "Nutricionista"
        );
        when(service.createLogin(request)).thenReturn(response);

        LoginNutriResponse created = authController.register(request);

        assertNotNull(created);
        assertEquals("login-1", created.id());
        assertEquals("nutritionist@professional.com.br", created.email());
    }

    @Test
    void shouldCreateProfile(){
        LoginRequest loginreq = new LoginRequest(
                "nutritionist@professional.com.br", "password"
        );

        when(service.authenticate(loginreq)).thenReturn("meu-token");

        ResponseEntity<?> response = authController.login(loginreq);

        Assertions.assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());


        Assertions.assertNotNull(response.getBody());

        verify(service).authenticate(loginreq);
    }
}
