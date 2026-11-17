package com.trainday.microserviceDiet.application.service;

import com.trainday.microserviceDiet.Repository.LoginNutritionistRepository;
import com.trainday.microserviceDiet.Repository.NutritionistRepository;
import com.trainday.microserviceDiet.api.DTO.request.NutriRequest;
import com.trainday.microserviceDiet.api.DTO.response.NutritionistResponse;
import com.trainday.microserviceDiet.models.LoginNutri;
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
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NutritionistServiceTest {
    @Mock
    private NutritionistRepository nutritionistRepository;
    @Mock
    private LoginNutritionistRepository loginRepository;
    @InjectMocks
    private NutritionistService service;

    @Test
    void createNutriCreatesProfileLinkedToLogin() {
        LoginNutri login = new LoginNutri();
        login.setCrn("CRN-1");
        login.setEmail("ana@example.com");
        NutriRequest request = new NutriRequest(
                "Ana", LocalDate.of(1990, 1, 1), "123", "555-0100",
                "Rua A", true
        );
        when(loginRepository.findByCrn("CRN-1")).thenReturn(Optional.of(login));
        when(nutritionistRepository.findByCrn("CRN-1")).thenReturn(Optional.empty());
        when(nutritionistRepository.save(any(Nutritionist.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Nutritionist created = service.createNutri("CRN-1", request);

        assertEquals("Ana", created.getName());
        assertEquals("ana@example.com", created.getEmail());
        assertEquals("CRN-1", created.getCrn());
        assertEquals(Role.NUTRITIONIST, created.getRole());
        verify(nutritionistRepository).save(any(Nutritionist.class));
    }

    @Test
    void createNutriRejectsExistingProfile() {
        LoginNutri login = new LoginNutri();
        login.setCrn("CRN-1");
        when(loginRepository.findByCrn("CRN-1")).thenReturn(Optional.of(login));
        when(nutritionistRepository.findByCrn("CRN-1"))
                .thenReturn(Optional.of(new Nutritionist()));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.createNutri("CRN-1", new NutriRequest(
                        "Ana", null, null, null, null, null
                ))
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verify(nutritionistRepository, never()).save(any());
    }

    @Test
    void searchNutritionistMapsProfileToResponse() {
        Nutritionist nutritionist = Nutritionist.builder()
                .id("nutritionist-1")
                .name("Ana")
                .email("ana@example.com")
                .crn("CRN-1")
                .role(Role.NUTRITIONIST)
                .build();
        when(nutritionistRepository.findByCrn("CRN-1")).thenReturn(Optional.of(nutritionist));

        NutritionistResponse response = service.searchNutritionist("CRN-1");

        assertEquals("nutritionist-1", response.id());
        assertEquals("Ana", response.name());
        assertEquals("ana@example.com", response.email());
        assertEquals("CRN-1", response.crn());
        assertEquals(Role.NUTRITIONIST, response.ROLE());
    }

    @Test
    void showAllNutriMapsAllProfilesToResponses() {
        Nutritionist nutritionist = Nutritionist.builder()
                .id("nutritionist-1")
                .name("Ana")
                .email("ana@example.com")
                .crn("CRN-1")
                .role(Role.NUTRITIONIST)
                .build();
        when(nutritionistRepository.findAll()).thenReturn(List.of(nutritionist));

        List<NutritionistResponse> responses = service.showAllNutri();

        assertEquals(1, responses.size());
        assertEquals("CRN-1", responses.get(0).crn());
        assertEquals("Ana", responses.get(0).name());
    }

    @Test
    void updateStatusUpdatesNonNullFieldsAndSavesProfile() {
        Nutritionist existing = Nutritionist.builder()
                .id("nutritionist-1")
                .name("Old name")
                .crn("CRN-1")
                .build();
        NutriRequest request = new NutriRequest(
                "New name", null, null, null, null, null
        );
        when(nutritionistRepository.findByCrn("CRN-1")).thenReturn(Optional.of(existing));
        when(nutritionistRepository.save(existing)).thenReturn(existing);

        Nutritionist updated = service.updateStatus("CRN-1", request);

        assertEquals("New name", updated.getName());
        verify(nutritionistRepository).save(existing);
    }

    @Test
    void patchStatusUpdatesNonNullFieldsAndSavesProfile() {
        Nutritionist existing = Nutritionist.builder()
                .id("nutritionist-1")
                .name("Old name")
                .crn("CRN-1")
                .build();
        NutriRequest request = new NutriRequest(
                "Patched name", null, null, null, null, null
        );
        when(nutritionistRepository.findByCrn("CRN-1")).thenReturn(Optional.of(existing));
        when(nutritionistRepository.save(existing)).thenReturn(existing);

        Nutritionist updated = service.patchStatus("CRN-1", request);

        assertEquals("Patched name", updated.getName());
        verify(nutritionistRepository).save(existing);
    }
}
