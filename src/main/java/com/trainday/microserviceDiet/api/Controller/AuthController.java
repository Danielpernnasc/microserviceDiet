package com.trainday.microserviceDiet.api.Controller;


import com.trainday.microserviceDiet.api.DTO.request.LoginRequest;
import com.trainday.microserviceDiet.api.DTO.request.RegisterNutritionistReq;
import com.trainday.microserviceDiet.api.DTO.response.LoginNutriResponse;
import com.trainday.microserviceDiet.application.service.LoginNutritionistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final LoginNutritionistService loginService;

    public AuthController(LoginNutritionistService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/register")
    public LoginNutriResponse register(@RequestBody RegisterNutritionistReq request) {

        return loginService.createLogin(request);

    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        String token = loginService.authenticate(request);
        return ResponseEntity.ok(token);
    }
}
