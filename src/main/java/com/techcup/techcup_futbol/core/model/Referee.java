package com.techcup.techcup_futbol.core.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Referee {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String fullname;
    private String email;

    // Un árbitro tiene muchos partidos asignados
    @OneToMany(mappedBy = "referee")
    private List<Match> assignedMatches;

    public Match getMatchDetails(String matchId) {
        return this.assignedMatches.stream()
                .filter(m -> m.getId().equals(matchId))
                .findFirst()
                .orElse(null);
    }

}
