package com.techcup.techcup_futbol.controller.dto;

public record MatchScheduleDTO(
        String dayOfWeek,
        String startTime,
        String endTime
) {}
