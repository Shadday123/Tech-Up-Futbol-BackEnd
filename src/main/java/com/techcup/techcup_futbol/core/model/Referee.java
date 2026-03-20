package com.techcup.techcup_futbol.core.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
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

    @Transient
    private List<Match> assignedMatches = new ArrayList<>();

    public Match getMatchDetails(String matchId) {
        if (assignedMatches == null) return null;
        return assignedMatches.stream()
                .filter(m -> m.getId().equals(matchId))
                .findFirst()
                .orElse(null);
    }
}