package com.techcup.techcup_futbol.core.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.persistence.DiscriminatorValue;

@EqualsAndHashCode(callSuper = true)
@Data
@DiscriminatorValue("EXTERNAL")
public class ExternalPlayer extends Player {
}
