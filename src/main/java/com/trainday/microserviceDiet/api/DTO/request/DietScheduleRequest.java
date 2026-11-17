package com.trainday.microserviceDiet.api.DTO.request;

import java.time.DayOfWeek;
import java.util.List;

public record DietScheduleRequest(
        String weekday,
        List<MealRequest> meals
) {

}
