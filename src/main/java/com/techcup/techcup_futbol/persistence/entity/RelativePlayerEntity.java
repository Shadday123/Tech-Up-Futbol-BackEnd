package com.techcup.techcup_futbol.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@EqualsAndHashCode(callSuper = true)
@Data
@DiscriminatorValue("RELATIVE")
public class RelativePlayerEntity extends PlayerEntity {

    private String parentship;
}