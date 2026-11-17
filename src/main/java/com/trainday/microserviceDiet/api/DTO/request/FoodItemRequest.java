package com.trainday.microserviceDiet.api.DTO.request;

public record FoodItemRequest(String food,
                              Double quantity,
                              String unit,
                              Double calories,
                              Double protein,
                              Double carbohydrates,
                              Double fat) {
}
