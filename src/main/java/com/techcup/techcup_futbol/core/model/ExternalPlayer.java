package com.techcup.techcup_futbol.core.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ExternalPlayer extends Player {
    String relationship;
    int relativeId;
}
