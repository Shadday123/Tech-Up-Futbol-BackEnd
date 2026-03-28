package com.techcup.techcup_futbol.core.model;

import jakarta.persistence.DiscriminatorValue;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.persistence.*;


@Entity
@EqualsAndHashCode(callSuper = true)
@Data
@DiscriminatorValue("STUDENT")
public class StudentPlayer extends Player {

    private int semester;
}
