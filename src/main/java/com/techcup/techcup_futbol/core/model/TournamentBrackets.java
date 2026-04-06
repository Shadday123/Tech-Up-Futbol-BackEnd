package com.techcup.techcup_futbol.core.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
public class TournamentBrackets {

    private String id;

    private Tournament tournament;

    private PhaseEnum phase;

    private List<Match> matches;

    public void promoteWinner(Match match) {
        // Lógica para pasar al ganador a la siguiente fase
    }
}
