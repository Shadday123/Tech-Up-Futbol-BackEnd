package com.techcup.techcup_futbol.Controller.dto;

import com.techcup.techcup_futbol.core.model.PositionEnum;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PlayerDTO {

    private String id;

    @NotBlank(message = "El nombre completo es obligatorio")
    private String fullname;

    @Email(message = "El correo debe ser válido")
    @NotBlank(message = "El correo es obligatorio")
    private String email;

    @Positive(message = "El número de identificación debe ser positivo")
    private int numberID;

    @NotNull(message = "La posición es obligatoria")
    private PositionEnum position;

    @Min(value = 1, message = "El número dorsal debe ser mayor a 0")
    private int dorsalNumber;

    private String photoUrl;

    private boolean haveTeam;

    private int age;

    @NotBlank(message = "El género es obligatorio")
    private String gender;

    private boolean captain;

    // "STUDENT", "INSTITUTIONAL", "RELATIVE"
    @NotBlank(message = "El tipo de jugador es obligatorio")
    private String playerType;

    private int semester; // solo aplica para StudentPlayer
}