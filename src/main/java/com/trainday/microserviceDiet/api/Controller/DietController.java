package com.trainday.microserviceDiet.api.Controller;

import com.trainday.microserviceDiet.api.DTO.request.DietRequest;
import com.trainday.microserviceDiet.application.service.DietaService;
import com.trainday.microserviceDiet.models.Diet;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Diet")
@SecurityRequirement(name = "bearerAuth")
public class DietController {
    private final DietaService dietService;

    public DietController(DietaService dietService) {
        this.dietService = dietService;
    }

    @PostMapping("/athletediet/post")
    public ResponseEntity<Diet> createDiet(
            @RequestBody DietRequest request,
            Authentication authentication
    ) {
        Diet createdDiet = dietService.createDiet(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDiet);
    }


    @GetMapping("/athletediet/get/{cpf}")
    public ResponseEntity<List<Diet>> getDietByAthlete(
            @RequestParam String cpf) {
        List<Diet> diets = dietService.getTrainByCpf(cpf);
        return ResponseEntity.ok(diets);
    }

    @PatchMapping("/athletediet/patch/{cpf}")
    public ResponseEntity<Diet> patchDietByCpf(@PathVariable String cpf, @RequestBody DietRequest req) {
        return ResponseEntity.ok(dietService.patchDietByCpf(cpf, req));
    }

    @PutMapping("/athletediet/put/{cpf}")
    public ResponseEntity<Diet> updateDietById(@PathVariable String cpf, @RequestBody DietRequest updateDietReq) {
        return ResponseEntity.ok(dietService.updateTrainByCpf(cpf, updateDietReq));
    }

    @DeleteMapping("/athletediet/delete/{cpf}")
    public ResponseEntity<Diet> deleteDietByCpf(@PathVariable String cpf) {
        Diet deletedDiet = dietService.deleteTrainByCpf(cpf);
        return ResponseEntity.ok(deletedDiet);
    }

}
