package com.techcup.techcup_futbol.core.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@EqualsAndHashCode(callSuper = true)
@Data
@DiscriminatorValue("RELATIVE")
public class RelativePlayer extends Player {

    private String parentship;
}
