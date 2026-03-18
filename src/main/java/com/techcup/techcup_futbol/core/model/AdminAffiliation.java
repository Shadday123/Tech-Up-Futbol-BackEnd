package com.techcup.techcup_futbol.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminAffiliation implements Affilation {
    private String department;
    private String area;

    @Override
    public String getType() {
        return "Administrativo";
    }
}
