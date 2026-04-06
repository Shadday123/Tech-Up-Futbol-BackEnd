package com.techcup.techcup_futbol.persistence.entity;

import jakarta.persistence.*;
import com.techcup.techcup_futbol.core.model.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "tournament_brackets")
public class TournamentBracketsEntity {

    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "tournament_id", nullable = false)
    private TournamentEntity tournament;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PhaseEnum phase;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "bracket_id")
    private List<MatchEntity> matches;

    public void promoteWinner(MatchEntity match) {
        // Lógica para pasar al ganador a la siguiente fase
    }
}