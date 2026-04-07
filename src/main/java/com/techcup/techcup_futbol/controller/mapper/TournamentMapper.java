package com.techcup.techcup_futbol.controller.mapper;

import com.techcup.techcup_futbol.controller.dto.FieldDTO;
import com.techcup.techcup_futbol.controller.dto.ImportantDateDTO;
import com.techcup.techcup_futbol.controller.dto.MatchScheduleDTO;
import com.techcup.techcup_futbol.controller.dto.TournamentConfigResponse;
import com.techcup.techcup_futbol.controller.dto.TournamentDTO;
import com.techcup.techcup_futbol.controller.dto.TournamentResponse;
import com.techcup.techcup_futbol.core.model.Tournament;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TournamentMapper {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;


    public static Tournament toModel(TournamentDTO dto) {

        if (dto == null){
            return null;
        }

        Tournament tournament = new Tournament();

        if (dto.getId() != null){
            tournament.setId(dto.getId());
        }
        tournament.setName(dto.getName());
        tournament.setStartDate(dto.getStartDate());
        tournament.setEndDate(dto.getEndDate());
        tournament.setRegistrationFee(dto.getRegistrationFee());
        tournament.setMaxTeams(dto.getMaxTeams());
        tournament.setRules(dto.getRules());
        tournament.setCurrentState(dto.getCurrentState());

        return tournament;
    }

    public static TournamentDTO toDTO(Tournament tournament) {

        if (tournament == null){
            return null;
        }

        TournamentDTO dto = new TournamentDTO();

        if (tournament.getId() != null){
            dto.setId(tournament.getId());
        }
        dto.setName(tournament.getName());
        dto.setStartDate(tournament.getStartDate());
        dto.setEndDate(tournament.getEndDate());
        dto.setRegistrationFee(tournament.getRegistrationFee());
        dto.setMaxTeams(tournament.getMaxTeams());
        dto.setRules(tournament.getRules());
        dto.setCurrentState(tournament.getCurrentState());

        return dto;
    }

    public static TournamentResponse toResponse(Tournament t) {
        return new TournamentResponse(
                t.getId(),
                t.getName(),
                t.getStartDate(),
                t.getEndDate(),
                t.getRegistrationFee(),
                t.getMaxTeams(),
                t.getRules(),
                t.getCurrentState().name()
        );
    }

    public static TournamentConfigResponse toConfigResponse(Tournament t) {
        List<ImportantDateDTO> dates = t.getImportantDates() == null ? List.of()
                : t.getImportantDates().stream().map(s -> {
            String[] parts = s.split("\\|", 2);
            return new ImportantDateDTO(parts[0],
                    parts.length > 1 && !parts[1].isBlank()
                            ? LocalDateTime.parse(parts[1], FMT) : null);
        }).toList();

        List<MatchScheduleDTO> schedules = t.getMatchSchedules() == null ? List.of()
                : t.getMatchSchedules().stream().map(s -> {
            String[] p = s.split("\\|", 3);
            return new MatchScheduleDTO(p[0],
                    p.length > 1 ? p[1] : "",
                    p.length > 2 ? p[2] : "");
        }).toList();

        List<FieldDTO> fields = t.getFields() == null ? List.of()
                : t.getFields().stream().map(s -> {
            String[] p = s.split("\\|", 2);
            return new FieldDTO(p[0], p.length > 1 ? p[1] : "");
        }).toList();

        return new TournamentConfigResponse(
                t.getConfigId(), t.getId(),
                t.getRules(), t.getRegistrationDeadline(),
                dates, schedules, fields, t.getSanctions()
        );
    }
}