package com.techcup.techcup_futbol.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ProfessorAffiliation implements Affilation {
    private String faculty;
    private List<String> subjects;

    @Override
    public String getType() {
        return "Profesor";
    }
}
