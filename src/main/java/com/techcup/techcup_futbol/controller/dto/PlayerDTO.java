package com.techcup.techcup_futbol.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d).+$",
            message = "La contraseña debe tener al menos una mayúscula y un número")
    private String password;

    @Positive(message = "El número de identificación debe ser positivo")
    private int numberID;

    @NotNull(message = "La posición es obligatoria")
    private PositionEnum position;

    @Min(value = 1, message = "El número dorsal debe ser mayor a 0")
    @Max(value = 99, message = "El número dorsal no puede superar 99")
    private int dorsalNumber;

    private String photoUrl;

    private boolean haveTeam;

    @Min(value = 15, message = "La edad mínima es 15")
    @Max(value = 110, message = "La edad máxima es 110")
    private int age;

    @NotBlank(message = "El género es obligatorio")
    private String gender;

    private boolean captain;
    @NotBlank(message = "El tipo de jugador es obligatorio")
    private String playerType;
    @Min(value = 1, message = "El semestre mínimo es 1")
    @Max(value = 10, message = "El semestre máximo es 10")
    private Integer semester;

    private String relationship;

    private String relationalId;
}