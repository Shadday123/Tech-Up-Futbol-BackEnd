package com.techcup.techcup_futbol.Controller.dto;
import com.techcup.techcup_futbol.core.model.TournamentState;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TournamentDTO {

    private String id;

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDateTime startDate;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDateTime endDate;

    @NotNull(message = "El costo de inscripción es obligatorio")
    @Positive
    private Double registrationFee;

    @Min(2)
    private int maxTeams;

    private String rules;

    private TournamentState currentState;

}
