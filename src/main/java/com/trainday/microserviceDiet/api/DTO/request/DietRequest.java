package com.trainday.microserviceDiet.api.DTO.request;

import com.trainday.microserviceDiet.models.DietSchedule;

import java.time.LocalDate;
import java.util.List;

public record DietRequest(
        String athleteCpf,
        String crn,
        String title,
        String objective,
        LocalDate startDate,
        LocalDate endDate,
        List<DietScheduleRequest> schedules
) {
}
