package com.trainday.microserviceDiet.models;


import com.trainday.microserviceDiet.models.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "diet")
public class Diet {
    @Id
    private String id;

    private String athleteId;
    private String athleteCpf;
    private String athleteName;
    private String athleteemail;
    private Role roleAthlete;


    private String nutritionistId;

    private String title; // Ex: Cutting, Bulking, Manutenção

    private String objective;

    private LocalDate startDate;

    private LocalDate endDate;

    private List<DietSchedule> schedules;

}
