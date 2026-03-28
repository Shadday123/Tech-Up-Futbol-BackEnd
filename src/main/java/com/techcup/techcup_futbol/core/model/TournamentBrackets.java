package com.techcup.techcup_futbol.core.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "tournament_brackets")
public class TournamentBrackets {

    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PhaseEnum phase;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "bracket_id")
    private List<Match> matches;

    public void promoteWinner(Match match) {
        // Lógica para pasar al ganador a la siguiente fase
    }
}
