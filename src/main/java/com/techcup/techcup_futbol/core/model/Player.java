package com.techcup.techcup_futbol.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player {

    private String id;

    private String fullname;
    private String email;

    @JsonIgnore
    private String passwordHash;

    private int numberID;

    private PositionEnum position;

    private int dorsalNumber;
    private String photoUrl;
    private boolean haveTeam;
    private boolean disponible = true;

    private int age;
    private String gender;
    private boolean captain;

    @JsonIgnore
    private Affilation affiliation;

    public void changeAvailability() {
        this.disponible = !this.disponible;
    }

    public void respondToInvitation(boolean accept) {
        if (accept) {
            this.haveTeam = true;
        }
    }
}
