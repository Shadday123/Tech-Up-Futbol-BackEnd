package com.techcup.techcup_futbol.Controller.dto;

public record MatchScheduleDTO(
        String dayOfWeek,
        String startTime,
        String endTime
) {}
