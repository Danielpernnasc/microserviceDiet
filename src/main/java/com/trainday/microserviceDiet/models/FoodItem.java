package com.trainday.microserviceDiet.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodItem {
    private String food;

    private Double quantity;

    private String unit;

    private Double calories;

    private Double protein;

    private Double carbohydrates;

    private Double fat;
}
