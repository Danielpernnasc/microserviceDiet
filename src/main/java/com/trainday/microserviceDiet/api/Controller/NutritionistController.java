package com.trainday.microserviceDiet.api.Controller;


import com.trainday.microserviceDiet.api.DTO.request.NutriRequest;
import com.trainday.microserviceDiet.api.DTO.response.NutritionistResponse;
import com.trainday.microserviceDiet.application.service.NutritionistService;
import com.trainday.microserviceDiet.models.Nutritionist;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Nutritionist")
@SecurityRequirement(name = "bearerAuth")
public class NutritionistController {

    private final NutritionistService service;

    public NutritionistController(NutritionistService service) {
        this.service= service;
    }

    @PostMapping
    public ResponseEntity<Nutritionist> save(
            @RequestBody NutriRequest req,
            Authentication authentication
            ){
        System.out.println("ENTROU NO CONTROLLER");
        String crn = authentication.getName();
        Nutritionist createdNutri = service.createNutri(crn, req);
        return  ResponseEntity.status(HttpStatus.CREATED)
                .body(createdNutri);
    }

    @GetMapping("/searchnutritionist/{crn}")
    public ResponseEntity<NutritionistResponse> getNutritionistByCrn(
            @PathVariable String crn) {
        return ResponseEntity.ok(service.searchNutritionist(crn));
    }

    @GetMapping("/ListNutri")
    public List<NutritionistResponse> getAllNutriRegistred(){
        return ResponseEntity.ok(service.showAllNutri()).getBody();
    }

    @PutMapping("/UpdateNutri")
    public Nutritionist updateNutri(
            @RequestBody NutriRequest req,
            Authentication authentication
    ){
        return service.updateStatus(authentication.getName(), req);
    }


    @PatchMapping("/PatchNutri")
        public Nutritionist patchNutri(
                @RequestBody NutriRequest req,
                Authentication authentication
                ){
            return service.patchStatus(authentication.getName(), req);
        }

    }




