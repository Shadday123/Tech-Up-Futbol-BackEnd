package com.techcup.techcup_futbol.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudentAffiliation implements Affilation  {
    private int semester;
    private String career;

    @Override
    public String getType() {
        return "Estudiante";
    }


}