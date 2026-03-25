package com.techcup.techcup_futbol.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name ="players")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name ="player_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Player {

    @Id
    private String id;

    @Column(nullable = false)
    private String fullname;
    private String email;

    @JsonIgnore
    private String passwordHash;

    private int numberID;

    @Enumerated(EnumType.STRING)
    private PositionEnum position;

    private int dorsalNumber;
    private String photoUrl;
    private boolean haveTeam;
    private int age;
    private String gender;
    private boolean captain;

    @Transient
    @JsonIgnore
    private Affilation affiliation;

    public void changeAvailability() {
        this.haveTeam = !this.haveTeam;
    }

    public void respondToInvitation(boolean accept) {
        if (accept) {
            this.haveTeam = true;
        }
    }
}