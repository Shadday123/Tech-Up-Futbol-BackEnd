package com.techcup.techcup_futbol.core.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Player {

    private String id;


    private String fullname;
    private String email;


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

    private SystemRole systemRole = SystemRole.JUGADOR;

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
