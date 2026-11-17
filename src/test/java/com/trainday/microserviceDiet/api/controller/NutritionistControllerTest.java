package com.trainday.microserviceDiet.api.controller;

import com.trainday.microserviceDiet.api.Controller.NutritionistController;
import com.trainday.microserviceDiet.api.DTO.request.NutriRequest;
import com.trainday.microserviceDiet.api.DTO.response.NutritionistResponse;
import com.trainday.microserviceDiet.application.service.NutritionistService;
import com.trainday.microserviceDiet.models.Nutritionist;
import com.trainday.microserviceDiet.models.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NutritionistControllerTest {
    @Mock
    private NutritionistService service;
    @Mock
    private Authentication authentication;

    private NutritionistController controller;
    private NutriRequest request;
    private Nutritionist nutritionist;

    @BeforeEach
    void setUp() {
        controller = new NutritionistController(service);
        request = new NutriRequest(
                "Mariana Oliveira Costa",
                LocalDate.of(1995, 5, 15),
                "12345678900",
                "11 99999-9999",
                "Rua Qualquer, 00",
                true
        );
        nutritionist = new Nutritionist();
        nutritionist.setId("nutritionist-1");
        nutritionist.setName(request.name());
        nutritionist.setEmail("mariana@example.com");
        nutritionist.setCrn("CRN-1");
        nutritionist.setCpf(request.cpf());
        nutritionist.setPhone(request.phone());
        nutritionist.setAddress(request.address());
        nutritionist.setBornDate(request.bornDate());
        nutritionist.setRole(Role.NUTRITIONIST);
    }

    @Test
    void createNutritionistReturnsCreatedAndUsesAuthenticatedCrn() {
        when(authentication.getName()).thenReturn("CRN-1");
        when(service.createNutri("CRN-1", request)).thenReturn(nutritionist);

        ResponseEntity<Nutritionist> response = controller.save(request, authentication);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertSame(nutritionist, response.getBody());
        verify(service).createNutri("CRN-1", request);
    }

    @Test
    void getNutritionistByCrnReturnsServiceResponse() {
        NutritionistResponse expected = new NutritionistResponse(
                "nutritionist-1", "Mariana Oliveira Costa",
                "mariana@example.com", "CRN-1", Role.NUTRITIONIST
        );
        when(service.searchNutritionist("CRN-1")).thenReturn(expected);

        ResponseEntity<NutritionistResponse> response =
                controller.getNutritionistByCrn("CRN-1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(expected, response.getBody());
        verify(service).searchNutritionist("CRN-1");
    }

    @Test
    void getAllNutritionistsReturnsServiceList() {
        List<NutritionistResponse> expected = List.of(new NutritionistResponse(
                "nutritionist-1", "Mariana Oliveira Costa",
                "mariana@example.com", "CRN-1", Role.NUTRITIONIST
        ));
        when(service.showAllNutri()).thenReturn(expected);

        List<NutritionistResponse> response = controller.getAllNutriRegistred();

        assertSame(expected, response);
        verify(service).showAllNutri();
    }

    @Test
    void updateNutritionistUsesAuthenticatedCrnAndRequest() {
        when(authentication.getName()).thenReturn("CRN-1");
        when(service.updateStatus("CRN-1", request)).thenReturn(nutritionist);

        Nutritionist response = controller.updateNutri(request, authentication);

        assertSame(nutritionist, response);
        verify(service).updateStatus("CRN-1", request);
    }

    @Test
    void patchNutritionistUsesAuthenticatedCrnAndRequest() {
        when(authentication.getName()).thenReturn("CRN-1");
        when(service.patchStatus("CRN-1", request)).thenReturn(nutritionist);

        Nutritionist response = controller.patchNutri(request, authentication);

        assertSame(nutritionist, response);
        verify(service).patchStatus("CRN-1", request);
    }
}
