package com.trainday.microserviceDiet.application.service;

import com.trainday.microserviceDiet.Repository.DietRepository;
import com.trainday.microserviceDiet.Repository.NutritionistRepository;
import com.trainday.microserviceDiet.api.DTO.request.DietRequest;
import com.trainday.microserviceDiet.api.DTO.request.DietScheduleRequest;
import com.trainday.microserviceDiet.api.DTO.request.FoodItemRequest;
import com.trainday.microserviceDiet.api.DTO.request.MealRequest;
import com.trainday.microserviceDiet.infra.DTO.response.AthleteCientResponse;
import com.trainday.microserviceDiet.infra.client.AthleteClient;
import com.trainday.microserviceDiet.models.Diet;
import com.trainday.microserviceDiet.models.Nutritionist;
import com.trainday.microserviceDiet.models.enums.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DietaServiceTest {
    @Mock
    private DietRepository dietRepository;
    @Mock
    private AthleteClient athleteClient;
    @Mock
    private NutritionistRepository nutritionistRepository;
    @InjectMocks
    private DietaService service;

    @Test
    void createDietSavesDietWithAthleteNutritionistAndSchedule() {
        Nutritionist nutritionist = Nutritionist.builder()
                .id("nutritionist-1")
                .name("Dra. Ana")
                .crn("CRN-1")
                .build();
        AthleteCientResponse athlete = new AthleteCientResponse(
                "athlete-1", "123", "Joao", null, "joao@example.com",
                null, null, null, null, null, Role.ATHLETE
        );
        DietScheduleRequest schedule = new DietScheduleRequest(
                "MONDAY",
                List.of(new MealRequest(
                        "meal-1",
                        "07:00",
                        "Breakfast",
                        List.of(new FoodItemRequest(
                                "Oats", 50.0, "grams", 190.0, 6.0, 32.0, 3.5
                        )),
                        "High-protein breakfast"
                ))
        );
        DietRequest request = new DietRequest(
                "123", "CRN-1", "Maintenance", "Healthy habits",
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 2, 1),
                List.of(schedule)
        );

        when(nutritionistRepository.findByCrn("CRN-1")).thenReturn(Optional.of(nutritionist));
        when(athleteClient.findByCpf("123")).thenReturn(athlete);
        when(dietRepository.save(any(Diet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Diet saved = service.createDiet(request, "CRN-1");

        assertEquals("athlete-1", saved.getAthleteId());
        assertEquals("123", saved.getAthleteCpf());
        assertEquals("nutritionist-1", saved.getNutritionistId());
        assertEquals("Dra. Ana", saved.getNameProfessional());
        assertEquals("Maintenance", saved.getTitle());
        assertEquals(DayOfWeek.MONDAY, saved.getSchedules().get(0).getDay());
        assertEquals(LocalTime.of(7, 0), saved.getSchedules().get(0).getMeals().get(0).getTime());
        assertEquals("Oats", saved.getSchedules().get(0).getMeals().get(0).getFoods().get(0).getFood());
        verify(dietRepository).save(any(Diet.class));
    }

    @Test
    void createDietRejectsInvalidDateRangeBeforeCallingDependencies() {
        DietRequest request = new DietRequest(
                "123", "CRN-1", "Diet", "Objective",
                LocalDate.of(2026, 2, 1), LocalDate.of(2026, 1, 1),
                List.of()
        );

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.createDiet(request, "CRN-1")
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verifyNoInteractions(dietRepository, athleteClient, nutritionistRepository);
    }

    @Test
    void patchDietPersistsChangedFields() {
        Diet existing = new Diet();
        existing.setTitle("Old title");
        when(dietRepository.findByAthleteCpf("123")).thenReturn(List.of(existing));
        when(dietRepository.save(existing)).thenReturn(existing);

        DietRequest request = new DietRequest(
                null, null, "New title", "New objective",
                null, null, null
        );

        Diet updated = service.patchDietByCpf("123", request);

        assertSame(existing, updated);
        assertEquals("New title", updated.getTitle());
        assertEquals("New objective", updated.getObjective());
        verify(dietRepository).save(existing);
    }

    @Test
    void getDietByCpfReturnsRepositoryResults() {
        List<Diet> diets = List.of(new Diet());
        when(dietRepository.findByAthleteCpf("123")).thenReturn(diets);

        assertSame(diets, service.getTrainByCpf("123"));
    }

    @Test
    void updateDietUpdatesFieldsAndSaves() {
        Diet existing = new Diet();
        existing.setTitle("Old title");
        Nutritionist nutritionist = Nutritionist.builder()
                .id("nutritionist-1")
                .name("Dra. Ana")
                .crn("CRN-1")
                .build();
        DietRequest request = new DietRequest(
                null, "CRN-1", "New title", "New objective",
                LocalDate.of(2026, 3, 1), null, null
        );
        when(dietRepository.findByAthleteCpf("123")).thenReturn(List.of(existing));
        when(nutritionistRepository.findByCrn("CRN-1")).thenReturn(Optional.of(nutritionist));
        when(dietRepository.save(existing)).thenReturn(existing);

        Diet updated = service.updateTrainByCpf("123", request);

        assertSame(existing, updated);
        assertEquals("New title", updated.getTitle());
        assertEquals("New objective", updated.getObjective());
        assertEquals(LocalDate.of(2026, 3, 1), updated.getStartDate());
        assertEquals("nutritionist-1", updated.getNutritionistId());
        verify(dietRepository).save(existing);
    }

    @Test
    void deleteDietDeletesAndReturnsDiet() {
        Diet existing = new Diet();
        when(dietRepository.findByAthleteCpf("123")).thenReturn(List.of(existing));

        Diet deleted = service.deleteTrainByCpf("123");

        assertSame(existing, deleted);
        verify(dietRepository).delete(existing);
    }
}
