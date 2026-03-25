package com.techcup.techcup_futbol.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Referee {

    private String id;

    private String fullname;
    private String email;

    private List<Match> assignedMatches = new ArrayList<>();

    public Match getMatchDetails(String matchId) {
        if (assignedMatches == null) return null;
        return assignedMatches.stream()
                .filter(m -> m.getId().equals(matchId))
                .findFirst()
                .orElse(null);
    }
}
