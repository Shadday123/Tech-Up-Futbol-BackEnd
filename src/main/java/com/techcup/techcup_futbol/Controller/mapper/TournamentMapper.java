package com.techcup.techcup_futbol.Controller.mapper;
import com.techcup.techcup_futbol.core.model.Tournament;
import com.techcup.techcup_futbol.Controller.dto.TournamentDTO;

public class TournamentMapper {

    public static Tournament toModel(TournamentDTO dto) {

        if (dto == null){
            return null;
        }

        Tournament tournament = new Tournament();

        tournament.setId(dto.getId());
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

        dto.setId(tournament.getId());
        dto.setName(tournament.getName());
        dto.setStartDate(tournament.getStartDate());
        dto.setEndDate(tournament.getEndDate());
        dto.setRegistrationFee(tournament.getRegistrationFee());
        dto.setMaxTeams(tournament.getMaxTeams());
        dto.setRules(tournament.getRules());
        dto.setCurrentState(tournament.getCurrentState());

        return dto;
    }

}