package com.techcup.techcup_futbol.core.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Player { // Ya no es abstracta

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String fullname;
    private String email;
    private int numberID;

    @Enumerated(EnumType.STRING)
    private PositionEnum position;

    private int dorsalNumber;
    private String photoUrl;
    private boolean haveTeam;
    private int age;
    private String gender;
    private boolean isCaptain;

    //  aplicamos la COMPOSICIÓN uso de la interfaz
    // @OneToOne indica que el jugador tiene una sola afilación

    @OneToOne(cascade = CascadeType.ALL)
    // crea una columna en las sql y tambien le da el mismo id que viene
    @JoinColumn(name = "affiliation_id", referencedColumnName = "id")
    private Affiliation affiliation;

    public void changeAvailability() {
        this.haveTeam = !this.haveTeam;
    }

    public void respondToInvitation(boolean accept) {
        if (accept) {
            this.haveTeam = true;
        }
    }
}