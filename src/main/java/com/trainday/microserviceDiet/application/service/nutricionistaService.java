package com.trainday.microserviceDiet.application.service;


import com.mongodb.DuplicateKeyException;
import com.trainday.microserviceDiet.Repository.LoginNutricionistRepository;
import com.trainday.microserviceDiet.Repository.NutricionistRepository;
import com.trainday.microserviceDiet.api.DTO.request.LoginRequest;
import com.trainday.microserviceDiet.api.DTO.request.NutriRequest;
import com.trainday.microserviceDiet.api.DTO.request.RegisterNutricionistReq;
import com.trainday.microserviceDiet.api.DTO.request.UpdateNutricionistaRequest;
import com.trainday.microserviceDiet.api.DTO.response.NutricionistResponse;
import com.trainday.microserviceDiet.models.LoginNutri;
import com.trainday.microserviceDiet.models.Nutricionist;
import com.trainday.microserviceDiet.models.enums.Role;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class nutricionistaService {

    private final LoginNutricionistRepository loginNutricionistRepository;
    private NutricionistRepository nutricionistRepository;
    public nutricionistaService(
            NutricionistRepository nutricionistRepository,
            LoginNutricionistRepository loginNutricionistRepository){
        this.nutricionistRepository = nutricionistRepository;
        this.loginNutricionistRepository = loginNutricionistRepository;
    }


    public Nutricionist createNutri(String email, NutriRequest req){
        LoginNutri user = loginNutricionistRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if(nutricionistRepository.findByCrn(email).isPresent()){
            throw new RuntimeException("CRN already registered");
        }

        Nutricionist nutri = new Nutricionist();
        nutri.setName(req.name());
        nutri.setEmail(user.getEmail());
        nutri.setCpf(req.cpf());
        nutri.setBornDate(req.bornDate());
        nutri.setAddress(req.address());
        nutri.setPhone(req.phone());
        nutri.setCrn(user.getCrn());
        nutri.setRole(Role.NUTRICIONIST);
        nutri.setActive(true);

        try {
            return nutricionistRepository.save(nutri);
        }catch (Exception e){
            throw new RuntimeException("Error creating Nutricionist " + e.getMessage());
        }

    }



    public NutricionistResponse searchNutricionist(String crn) {

        var nutricionist = nutricionistRepository.findByCrn(crn)
                .orElseThrow(() -> new RuntimeException("Nutricionista não encontrado"));


        return new NutricionistResponse(
                nutricionist.getId(),
                nutricionist.getName(),
                nutricionist.getEmail(),
                nutricionist.getCrn(),
                nutricionist.getRole()
        );

    }

    public List<NutricionistResponse> showAllNutri(){
        return nutricionistRepository.findAll()
                .stream()
                .map(nutricionist ->  new NutricionistResponse(
                        nutricionist.getId(),
                        nutricionist.getName(),
                        nutricionist.getEmail(),
                        nutricionist.getCrn(),
                        nutricionist.getRole()


                ))
                .toList();
    }

    public Nutricionist updateStatus(String crn, UpdateNutricionistaRequest req){


        Nutricionist nutri = nutricionistRepository.findByCrn(crn)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional.ofNullable(req.name()).ifPresent(nutri::setName);
        Optional.ofNullable(req.address()).ifPresent(nutri::setAddress);
        Optional.ofNullable(req.phone()).ifPresent(nutri::setPhone);
        Optional.ofNullable(req.cpf()).ifPresent(nutri::setCpf);


        try {
            return nutricionistRepository.save(nutri);
        } catch (DuplicateKeyException e) {
            throw new RuntimeException("CREF or Email already registered");
        }

    }



}
