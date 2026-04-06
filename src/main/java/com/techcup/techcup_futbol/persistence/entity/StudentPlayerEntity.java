package com.techcup.techcup_futbol.persistence.entity;

import jakarta.persistence.DiscriminatorValue;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.persistence.*;


@Entity
@EqualsAndHashCode(callSuper = true)
@Data
@DiscriminatorValue("STUDENT")
public class StudentPlayerEntity extends PlayerEntity {

    private int semester;
}