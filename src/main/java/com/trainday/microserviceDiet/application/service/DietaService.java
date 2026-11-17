package com.trainday.microserviceDiet.application.service;

import com.trainday.microserviceDiet.Repository.DietRepository;
import com.trainday.microserviceDiet.Repository.NutritionistRepository;
import com.trainday.microserviceDiet.api.DTO.request.DietRequest;
import com.trainday.microserviceDiet.api.DTO.request.DietScheduleRequest;
import com.trainday.microserviceDiet.api.DTO.request.FoodItemRequest;
import com.trainday.microserviceDiet.api.DTO.request.MealRequest;
import com.trainday.microserviceDiet.infra.DTO.response.AthleteCientResponse;
import com.trainday.microserviceDiet.infra.client.AthleteClient;
import com.trainday.microserviceDiet.models.Diet;
import com.trainday.microserviceDiet.models.DietSchedule;
import com.trainday.microserviceDiet.models.FoodItem;
import com.trainday.microserviceDiet.models.Meal;
import com.trainday.microserviceDiet.models.Nutritionist;
import com.trainday.microserviceDiet.models.enums.Role;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@Service
public class DietaService {
    private final DietRepository dietRepository;
    private final AthleteClient athleteClient;
    private final NutritionistRepository nutritionistRepository;

    public DietaService(DietRepository dietRepository, AthleteClient athleteClient, NutritionistRepository nutritionistRepository) {
        this.dietRepository = dietRepository;
        this.athleteClient = athleteClient;
        this.nutritionistRepository = nutritionistRepository;
    }

    public Diet createDiet(DietRequest req, String crn) {
        if (req.startDate() != null && req.endDate() != null
                && req.startDate().isAfter(req.endDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date must not be after end date");
        }

        // Verifica se o atleta existe no Athlete Service
        AthleteCientResponse athlete = athleteClient.findByCpf(req.athleteCpf());

        if (athlete == null) {
            throw new RuntimeException("Athlete not found");
        }

        Nutritionist professional =
                nutritionistRepository.findByCrn(crn)
                        .orElseThrow(() ->
                                new RuntimeException("Professional not found"));

        Diet diet = new Diet();

        diet.setAthleteId(athlete.id());
        diet.setAthleteCpf(athlete.cpf());
        diet.setAthleteName(athlete.name());
        diet.setAthleteemail(athlete.email());
        diet.setRoleAthlete(athlete.role());

        diet.setNutritionistId(professional.getId());
        diet.setCrn(professional.getCrn());
        diet.setNameProfessional(professional.getName());

        diet.setTitle(req.title());
        diet.setObjective(req.objective());
        diet.setStartDate(req.startDate());
        diet.setEndDate(req.endDate());
        diet.setSchedules(toDietSchedules(req.schedules()));

        return dietRepository.save(diet);
    }

    public List<Diet> getTrainByCpf(String cpf) {

        return dietRepository.findByAthleteCpf(cpf);
    }

    public Diet patchDietByCpf(String cpf, DietRequest req){
        List<Diet> diets = dietRepository.findByAthleteCpf(cpf);
        Diet diet = diets.stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Diet not found"));
        if(req.title() != null) {
            diet.setTitle(req.title());
        }
        if(req.objective() != null) {
            diet.setObjective(req.objective());
        }
        if(req.startDate() != null) {
            diet.setStartDate(req.startDate());
        }
        if(req.endDate() != null) {
            diet.setEndDate(req.endDate());
        }
        if(req.schedules() != null) {
            diet.setSchedules(toDietSchedules(req.schedules()));
        }
        return dietRepository.save(diet);
    }

    public Diet updateTrainByCpf(String cpf, DietRequest updateDietReq) {
        List<Diet> diets = dietRepository.findByAthleteCpf(cpf);
        Diet diet = diets.stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Train not found"));

        Nutritionist professional = nutritionistRepository.findByCrn(updateDietReq.crn())
                .orElseThrow(() -> new RuntimeException("Professional not found"));

        diet.setNutritionistId(professional.getId());
        diet.setCrn(professional.getCrn());
        diet.setNameProfessional(professional.getName());

        Optional.ofNullable(updateDietReq.title()).ifPresent(diet::setTitle);
        Optional.ofNullable(updateDietReq.schedules())
                .map(this::toDietSchedules)
                .ifPresent(diet::setSchedules);
        Optional.ofNullable(updateDietReq.objective()).ifPresent(diet::setObjective);
        Optional.ofNullable(updateDietReq.startDate()).ifPresent(diet::setStartDate);
        return dietRepository.save(diet);
    }

    public Diet deleteTrainByCpf(String cpf) {
        List<Diet> trains = dietRepository.findByAthleteCpf(cpf);
        Diet diet = trains.stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Train not found"));
        dietRepository.delete(diet);
        return diet;
    }

    private boolean isInvalidSchedule(DietSchedule schedule) {
        return Objects.isNull(schedule)
                || schedule.getDay() == null
                || schedule.getMeals() == null
                || schedule.getMeals().isEmpty()
                || schedule.getMeals().stream().anyMatch(meal ->
                        meal == null || meal.getTime() == null
                                || meal.getName() == null || meal.getName().isBlank());
    }

    private List<DietSchedule> toDietSchedules(List<DietScheduleRequest> schedules) {
        if (schedules == null) {
            return null;
        }
        return schedules.stream()
                .map(schedule -> new DietSchedule(
                        DayOfWeek.valueOf(schedule.weekday().toUpperCase(Locale.ROOT)),
                        schedule.meals() == null ? null : schedule.meals().stream()
                                .map(this::toMeal)
                                .toList()
                ))
                .toList();
    }

    private Meal toMeal(MealRequest request) {
        return new Meal(
                request.id(),
                request.time() == null ? null : LocalTime.parse(request.time()),
                request.name(),
                request.foods() == null ? null : request.foods().stream()
                        .map(this::toFoodItem)
                        .toList(),
                request.observation()
        );
    }

    private FoodItem toFoodItem(FoodItemRequest request) {
        return new FoodItem(
                request.food(),
                request.quantity(),
                request.unit(),
                request.calories(),
                request.protein(),
                request.carbohydrates(),
                request.fat()
        );
    }
}
