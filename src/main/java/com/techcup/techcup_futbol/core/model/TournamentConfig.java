package com.techcup.techcup_futbol.core.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class TournamentConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne
    private Tournament tournament;

    @Column(columnDefinition = "TEXT")
    private String rules;

    private LocalDateTime registrationDeadline;

    @ElementCollection
    private List<String> importantDates;

    @ElementCollection
    private List<String> matchSchedules;

    @ElementCollection
    private List<String> fields;

    @Column(columnDefinition = "TEXT")
    private String sanctions;
}
