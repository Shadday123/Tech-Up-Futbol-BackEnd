package com.techcup.techcup_futbol.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExternalAffiliation implements Affilation {
    private String relationshipType;
    private String associatedInternalId;

    @Override
    public String getType() {
        return "Externo/Familiar";
    }
}
