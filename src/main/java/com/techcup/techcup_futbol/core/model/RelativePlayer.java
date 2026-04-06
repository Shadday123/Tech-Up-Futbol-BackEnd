package com.techcup.techcup_futbol.core.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RelativePlayer extends Player {

    private String parentship;
}
