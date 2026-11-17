package com.trainday.microserviceDiet.api.DTO.request;

import java.util.List;

public record MealRequest(
        String id,
        String time,
        String name,
        List<FoodItemRequest> foods,
        String observation
) {
}
