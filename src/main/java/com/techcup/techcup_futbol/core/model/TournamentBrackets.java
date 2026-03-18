package com.techcup.techcup_futbol.core.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class TournamentBrackets {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    private Tournament tournament;

    @Enumerated(EnumType.STRING)
    private PhaseEnum phase;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Match> matches;

    public void promoteWinner(Match match) {
        // Lógica para pasar al ganador a la siguiente fase
    }
}
