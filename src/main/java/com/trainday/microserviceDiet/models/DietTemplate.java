package com.trainday.microserviceDiet.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "diet_templates")
public class DietTemplate {
    @Id
    private String id;
    private String athleteId;
    private String nameTrain;
    private String objective;
    private String category;
    private String description;
    @CreatedDate
    private LocalDateTime createdAt;
    private List<DietSchedule> schedules;
}
