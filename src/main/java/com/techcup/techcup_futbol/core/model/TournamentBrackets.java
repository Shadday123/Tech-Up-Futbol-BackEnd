package com.techcup.techcup_futbol.core.model;

import lombok.Data;

import java.util.List;

@Data
public class TournamentBrackets {

    private String id;

    private Tournament tournament;

    private PhaseEnum phase;

    private List<Match> matches;

}
