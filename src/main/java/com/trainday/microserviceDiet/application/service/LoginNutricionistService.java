package com.trainday.microserviceDiet.application.service;

import com.trainday.microserviceDiet.Repository.LoginNutricionistRepository;
import com.trainday.microserviceDiet.Repository.NutricionistRepository;
import com.trainday.microserviceDiet.api.DTO.request.LoginRequest;
import com.trainday.microserviceDiet.api.DTO.request.RegisterNutricionistReq;
import com.trainday.microserviceDiet.api.DTO.response.LoginNutriResponse;
import com.trainday.microserviceDiet.infra.service.JwtService;
import com.trainday.microserviceDiet.models.LoginNutri;
import com.trainday.microserviceDiet.models.Nutricionist;
import com.trainday.microserviceDiet.models.enums.Role;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.PasswordAuthentication;
import java.util.Optional;

@Service
public class LoginNutricionistService {
    private JwtService jwtService;
    private LoginNutricionistRepository loginNutricionistRepository;
    private NutricionistRepository nutricionistRepository;
    private final AuthenticationManager autenticationManager;
    private final PasswordEncoder passwordEncoder;

    public LoginNutricionistService(
            JwtService jwtService,
            LoginNutricionistRepository loginNutricionistRepository,
            NutricionistRepository nutricionistRepository,
            AuthenticationManager autenticationManager,
            PasswordEncoder passwordEncoder
       ) {
        this.jwtService = jwtService;
        this.loginNutricionistRepository = loginNutricionistRepository;
        this.nutricionistRepository = nutricionistRepository;
        this.autenticationManager = autenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginNutriResponse createLogin(RegisterNutricionistReq request) {

        if (loginNutricionistRepository.existsByCrn(request.crn()) || loginNutricionistRepository.existsByEmail(request.email())) {
            throw new RuntimeException("CRN or Email already exists");
        }

        LoginNutri login = new LoginNutri();
        login.setEmail(request.email());
        login.setCrn(request.crn());
        login.setRole(request.role());
        login.setPassword(request.password());
        login.setPassword(passwordEncoder.encode(request.password()));

        LoginNutri saved = loginNutricionistRepository.save(login);


        return new LoginNutriResponse(
                saved.getId(),
                saved.getEmail(),
                saved.getCrn(),
                saved.getRole().getState()
        );

    }

    public String authenticate(LoginRequest request){

        Optional<LoginNutri> user = loginNutricionistRepository.findByEmail(request.login());

        if(user.isEmpty()){
            user = loginNutricionistRepository.findByCrn(request.login());
        }

        LoginNutri login = user.orElseThrow(() -> new RuntimeException("User not found"));

        if(!passwordEncoder.matches(request.password(), login.getPassword())){
            throw new RuntimeException("Invalid credentials");
        }

        try {
//            autenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(
//                            request.login(),
//                            request.password()));
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }

        Optional<Nutricionist> nutri = nutricionistRepository.findByCrn((login.getCrn()));

        String professionalId = nutri
                .map(Nutricionist::getId)
                .orElse(null);

        Role role = nutri
                .map(Nutricionist::getRole)
                .orElse(Role.NUTRICIONIST);
        return jwtService.generateToken(
                login.getEmail(),
                login.getCrn(),
                login.getId(),
                role);
        }
    }




