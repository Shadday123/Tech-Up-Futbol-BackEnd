package com.techcup.techcup_futbol.Controller.dto;

import java.time.LocalDateTime;
import java.util.List;

public record TournamentConfigResponse(
        String id,
        String tournamentId,
        String rules,
        LocalDateTime registrationDeadline,
        List<ImportantDateDTO> importantDates,
        List<MatchScheduleDTO> matchSchedules,
        List<FieldDTO> fields,
        String sanctions
) {}
