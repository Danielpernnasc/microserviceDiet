package com.trainday.microserviceDiet.application.service;

import com.trainday.microserviceDiet.Repository.LoginNutritionistRepository;
import com.trainday.microserviceDiet.Repository.NutritionistRepository;
import com.trainday.microserviceDiet.api.DTO.request.LoginRequest;
import com.trainday.microserviceDiet.api.DTO.request.RegisterNutritionistReq;
import com.trainday.microserviceDiet.api.DTO.response.LoginNutriResponse;
import com.trainday.microserviceDiet.infra.service.JwtService;
import com.trainday.microserviceDiet.models.LoginNutri;
import com.trainday.microserviceDiet.models.Nutritionist;
import com.trainday.microserviceDiet.models.enums.Role;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class LoginNutritionistService {
    private final JwtService jwtService;
    private final LoginNutritionistRepository loginNutritionistRepository;
    private final NutritionistRepository nutritionistRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginNutritionistService(
            JwtService jwtService,
            LoginNutritionistRepository loginNutritionistRepository,
            NutritionistRepository nutritionistRepository,
            PasswordEncoder passwordEncoder
       ) {
        this.jwtService = jwtService;
        this.loginNutritionistRepository = loginNutritionistRepository;
        this.nutritionistRepository = nutritionistRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginNutriResponse createLogin(RegisterNutritionistReq request) {

        if (loginNutritionistRepository.existsByCrn(request.crn()) || loginNutritionistRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CRN ou e-mail já cadastrado");
        }

        LoginNutri login = new LoginNutri();
        login.setEmail(request.email());
        login.setCrn(request.crn());
        login.setRole(request.role());
        login.setPassword(request.password());
        login.setPassword(passwordEncoder.encode(request.password()));

        LoginNutri saved = loginNutritionistRepository.save(login);


        return new LoginNutriResponse(
                saved.getId(),
                saved.getEmail(),
                saved.getCrn(),
                saved.getRole().getState()
        );

    }

    public String authenticate(LoginRequest request){

        Optional<LoginNutri> user = loginNutritionistRepository.findByEmail(request.login());

        if(user.isEmpty()){
            user = loginNutritionistRepository.findByCrn(request.login());
        }

        LoginNutri login = user.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não encontrado"));

        if(!passwordEncoder.matches(request.password(), login.getPassword())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Senha inválida");
        }

        Optional<Nutritionist> nutri = nutritionistRepository.findByCrn(login.getCrn());

        Role role = nutri
                .map(Nutritionist::getRole)
                .orElse(Role.NUTRITIONIST);
        return jwtService.generateToken(
                login.getEmail(),
                login.getCrn(),
                login.getId(),
                role);
        }
    }




