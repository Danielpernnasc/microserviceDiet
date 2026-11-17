package com.trainday.microserviceDiet.application.service;


import com.mongodb.DuplicateKeyException;

import com.trainday.microserviceDiet.Repository.LoginNutritionistRepository;
import com.trainday.microserviceDiet.Repository.NutritionistRepository;
import com.trainday.microserviceDiet.api.DTO.request.NutriRequest;
import com.trainday.microserviceDiet.api.DTO.response.NutritionistResponse;
import com.trainday.microserviceDiet.models.LoginNutri;
import com.trainday.microserviceDiet.models.Nutritionist;
import com.trainday.microserviceDiet.models.enums.Role;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class NutritionistService {

    private final LoginNutritionistRepository loginNutritionistRepository;
    private final NutritionistRepository nutritionistRepository;
    public NutritionistService(
            NutritionistRepository nutritionistRepository,
            LoginNutritionistRepository loginNutritionistRepository){
        this.nutritionistRepository = nutritionistRepository;
        this.loginNutritionistRepository = loginNutritionistRepository;
    }


    public Nutritionist createNutri(String crn, NutriRequest req){
        LoginNutri user = loginNutritionistRepository.findByCrn(crn)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if(nutritionistRepository.findByCrn(user.getCrn()).isPresent()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este login já possui um nutricionista cadastrado");
        }

        Nutritionist nutri = new Nutritionist();
        nutri.setName(req.name());
        nutri.setEmail(user.getEmail());
        nutri.setCpf(req.cpf());
        nutri.setBornDate(req.bornDate());
        nutri.setAddress(req.address());
        nutri.setPhone(req.phone());
        nutri.setCrn(user.getCrn());
        nutri.setRole(Role.NUTRITIONIST);
        nutri.setActive(true);

        try {
            return nutritionistRepository.save(nutri);
        }catch (Exception e){
            throw new RuntimeException("Error creating Nutritionist " + e.getMessage());
        }

    }



    public NutritionistResponse searchNutritionist(String crn) {

        var nutricionist = nutritionistRepository.findByCrn(crn)
                .orElseThrow(() -> new RuntimeException("Nutricionista não encontrado"));


        return new NutritionistResponse(
                nutricionist.getId(),
                nutricionist.getName(),
                nutricionist.getEmail(),
                nutricionist.getCrn(),
                nutricionist.getRole()
        );

    }

    public List<NutritionistResponse> showAllNutri(){
        return nutritionistRepository.findAll()
                .stream()
                .map(nutricionist ->  new NutritionistResponse(
                        nutricionist.getId(),
                        nutricionist.getName(),
                        nutricionist.getEmail(),
                        nutricionist.getCrn(),
                        nutricionist.getRole()


                ))
                .toList();
    }

    public Nutritionist updateStatus(String crn, NutriRequest req){
        System.out.println("Email recebido = [" + crn + "]");

        nutritionistRepository.findAll().forEach(n ->
                System.out.println("Banco -> " + n.getEmail())
        );

        Nutritionist nutri = nutritionistRepository.findByCrn(crn)
                .orElseThrow(() -> new RuntimeException("Nutricionista não encontrado"));

        Optional.ofNullable(req.name()).ifPresent(nutri::setName);
        Optional.ofNullable(req.bornDate()).ifPresent(nutri::setBornDate);
        Optional.ofNullable(req.address()).ifPresent(nutri::setAddress);
        Optional.ofNullable(req.phone()).ifPresent(nutri::setPhone);
        Optional.ofNullable(req.cpf()).ifPresent(nutri::setCpf);

        try {
            return nutritionistRepository.save(nutri);
        } catch (DuplicateKeyException e) {
            throw new RuntimeException("CREF or Email already registered");
        }

    }

    public Nutritionist patchStatus(String crn, NutriRequest req){

        System.out.println("CRN recebido = [" + crn + "]");

        nutritionistRepository.findAll().forEach(n ->
                System.out.println("Banco -> " + n.getCrn())
        );
        Nutritionist nutri = nutritionistRepository.findByCrn(crn)
                .orElseThrow(() -> new RuntimeException("Nutricionist not found"));

        if (req.name() != null) {
            nutri.setName(req.name());
        }

        if (req.bornDate() != null) {
            nutri.setBornDate(req.bornDate());
        }

        if (req.cpf() != null) {
            nutri.setCpf(req.cpf());
        }

        if (req.phone() != null) {
            nutri.setPhone(req.phone());
        }

        if (req.address() != null) {
            nutri.setAddress(req.address());
        }


        try {
            return nutritionistRepository.save(nutri);
        } catch (DuplicateKeyException e) {
            throw new RuntimeException("CREF or Email already registered");
        }

    }

}
