package com.techcup.techcup_futbol.core.model;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class ExternalPlayer extends Player {

    @Override
    public void changeAvailability() {
        this.haveTeam = !this.haveTeam;
    }

    @Override
    public void respondToInvitation(boolean accept) {
        if (accept) {
            this.haveTeam = true;
        }
    }
}