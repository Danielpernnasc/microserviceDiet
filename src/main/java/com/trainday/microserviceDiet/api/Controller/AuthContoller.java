package com.trainday.microserviceDiet.api.Controller;


import com.trainday.microserviceDiet.api.DTO.request.LoginRequest;
import com.trainday.microserviceDiet.api.DTO.request.RegisterNutricionistReq;
import com.trainday.microserviceDiet.api.DTO.response.LoginNutriResponse;
import com.trainday.microserviceDiet.application.service.LoginNutricionistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthContoller {
    private final LoginNutricionistService loginService;

    public AuthContoller(LoginNutricionistService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/register")
    public LoginNutriResponse register(@RequestBody RegisterNutricionistReq request) {

        return loginService.createLogin(request);

    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        String token = loginService.authenticate(request);
        return ResponseEntity.ok(token);
    }
}
