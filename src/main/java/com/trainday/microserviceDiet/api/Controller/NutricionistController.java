package com.trainday.microserviceDiet.api.Controller;


import com.trainday.microserviceDiet.api.DTO.request.NutriRequest;
import com.trainday.microserviceDiet.api.DTO.request.RegisterNutricionistReq;
import com.trainday.microserviceDiet.api.DTO.request.UpdateNutricionistaRequest;
import com.trainday.microserviceDiet.api.DTO.response.NutricionistResponse;
import com.trainday.microserviceDiet.application.service.nutricionistaService;
import com.trainday.microserviceDiet.models.Nutricionist;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Nutricionist")
@SecurityRequirement(name = "bearerAuth")
public class NutricionistController {

    private final nutricionistaService service;

    public NutricionistController(nutricionistaService service) {
        this.service= service;
    }

    @PostMapping
    public ResponseEntity<Nutricionist> save(
            @RequestBody NutriRequest req,
            Authentication authentication
            ){
        System.out.println("ENTROU NO CONTROLLER");
        String crn = authentication.getName();
        Nutricionist createdNutri = service.createNutri(crn, req);
        return  ResponseEntity.status(HttpStatus.CREATED)
                .body(createdNutri);
    }

    @GetMapping("/searchnutricionist/{crn}")
    public ResponseEntity<NutricionistResponse> getNutricionistByCrn(
            @PathVariable String crn) {
        return ResponseEntity.ok(service.searchNutricionist(crn));
    }

    @GetMapping("/ListNutri")
    public List<NutricionistResponse> getAllNutriRegistred(){
        return ResponseEntity.ok(service.showAllNutri()).getBody();
    }

    @PutMapping("/UpdateNutri/{crn}")
    public Nutricionist updateNutri(
            @RequestBody UpdateNutricionistaRequest req,
            Authentication authentication
            ){
        return service.updateStatus(authentication.getName(), req);

    }


}
