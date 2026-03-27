package com.techcup.techcup_futbol.core.model;

import jakarta.persistence.DiscriminatorValue;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@DiscriminatorValue("RELATIVE")
public class RelativePlayer extends Player {
}
