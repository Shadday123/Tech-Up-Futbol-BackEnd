package com.techcup.techcup_futbol.persistence.entity;

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
public class RefereeEntity {

    @Id
    private String id;

    private String fullname;
    private String email;
    private String passwordHash;
    private String license;
    private int experience;

    @OneToMany(mappedBy = "referee", fetch = FetchType.LAZY)
    private List<MatchEntity> assignedMatches = new ArrayList<>();

    public MatchEntity getMatchDetails(String matchId) {
        if (assignedMatches == null) return null;
        return assignedMatches.stream()
                .filter(m -> m.getId().equals(matchId))
                .findFirst()
                .orElse(null);
    }
}