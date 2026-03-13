package com.techcup.techcup_futbol.core.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    protected String id;

    protected String fullname;
    protected String email;
    protected int numberID;

    @Enumerated(EnumType.STRING)
    protected PositionEnum position;

    protected int dorsalNumber;

    protected String photoUrl;

    protected boolean haveTeam;

    protected int age;

    protected String gender;

    protected boolean captain;

    public abstract void changeAvailability();

    public abstract void respondToInvitation(boolean accept);

}