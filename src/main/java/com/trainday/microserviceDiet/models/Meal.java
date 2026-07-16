package com.trainday.microserviceDiet.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Meal {
    private String id;

    private LocalTime time;

    private String name;

    private List<FoodItem> foods;

    private String observation;
}

