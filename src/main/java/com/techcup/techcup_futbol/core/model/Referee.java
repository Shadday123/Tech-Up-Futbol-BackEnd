package com.techcup.techcup_futbol.core.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "referees")
public class Referee {

    @Id
    private String id;

    private String fullname;
    private String email;

    @OneToMany(mappedBy = "referee", fetch = FetchType.LAZY)
    private List<Match> assignedMatches = new ArrayList<>();

    public Match getMatchDetails(String matchId) {
        if (assignedMatches == null) return null;
        return assignedMatches.stream()
                .filter(m -> m.getId().equals(matchId))
                .findFirst()
                .orElse(null);
    }
}
