package com.trainday.microserviceDiet.api.controller;

import com.trainday.microserviceDiet.api.Controller.DietController;
import com.trainday.microserviceDiet.api.DTO.request.DietRequest;
import com.trainday.microserviceDiet.api.DTO.request.DietScheduleRequest;
import com.trainday.microserviceDiet.api.DTO.request.FoodItemRequest;
import com.trainday.microserviceDiet.api.DTO.request.MealRequest;
import com.trainday.microserviceDiet.application.service.DietaService;
import com.trainday.microserviceDiet.models.Diet;
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
class DietControllerTest {
    @Mock
    private DietaService dietService;
    @Mock
    private Authentication authentication;

    private DietController controller;
    private DietRequest request;
    private Diet diet;

    @BeforeEach
    void setUp() {
        controller = new DietController(dietService);
        request = new DietRequest(
                "12345678900",
                "CRN-123",
                "Ganho de massa",
                "Aumentar massa muscular",
                LocalDate.of(2026, 11, 13),
                LocalDate.of(2027, 2, 13),
                List.of(new DietScheduleRequest("MONDAY", List.of(
                        new MealRequest("meal-1", "07:00", "Café da manhã", List.of(
                                new FoodItemRequest("Ovos", 2.0, "unidades",
                                        140.0, 12.0, 1.0, 10.0)
                        ), "Refeição com proteína")
                )))
        );
        diet = new Diet();
        diet.setId("diet-1");
    }

    @Test
    void createDietReturnsCreatedAndPassesAuthenticatedCrn() {
        when(authentication.getName()).thenReturn("CRN-123");
        when(dietService.createDiet(request, "CRN-123")).thenReturn(diet);

        ResponseEntity<Diet> response = controller.createDiet(request, authentication);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertSame(diet, response.getBody());
        verify(dietService).createDiet(request, "CRN-123");
    }

    @Test
    void getDietByAthleteReturnsDietList() {
        List<Diet> diets = List.of(diet);
        when(dietService.getTrainByCpf("12345678900")).thenReturn(diets);

        ResponseEntity<List<Diet>> response = controller.getDietByAthlete("12345678900");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(diets, response.getBody());
        verify(dietService).getTrainByCpf("12345678900");
    }

    @Test
    void patchDietReturnsUpdatedDiet() {
        when(dietService.patchDietByCpf("12345678900", request)).thenReturn(diet);

        ResponseEntity<Diet> response = controller.patchDietByCpf("12345678900", request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(diet, response.getBody());
        verify(dietService).patchDietByCpf("12345678900", request);
    }

    @Test
    void updateDietReturnsUpdatedDiet() {
        when(dietService.updateTrainByCpf("12345678900", request)).thenReturn(diet);

        ResponseEntity<Diet> response = controller.updateDietById("12345678900", request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(diet, response.getBody());
        verify(dietService).updateTrainByCpf("12345678900", request);
    }

    @Test
    void deleteDietReturnsDeletedDiet() {
        when(dietService.deleteTrainByCpf("12345678900")).thenReturn(diet);

        ResponseEntity<Diet> response = controller.deleteDietByCpf("12345678900");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(diet, response.getBody());
        verify(dietService).deleteTrainByCpf("12345678900");
    }
}
