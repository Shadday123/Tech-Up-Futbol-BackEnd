package com.techcup.techcup_futbol.controller.dto;

import com.techcup.techcup_futbol.core.model.Player;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;

@Data
public class TeamDTO {

    @NotBlank(message = "El campo de ID debe ser obligatorio")
    private String id;

    @NotBlank(message = "El nombre del equipo es obligatorio")
    private String teamName;

    @NotBlank(message = "La imagen del equipo es obligatoria ")
    private String shieldUrl;

    @NotEmpty(message = "Los colores son obligatorios")
    private List<String> uniformColors;

    @NotBlank(message = "Debe haber un capitan")
    private Player captain;

    @NotBlank(message = "Los integrantes son obligatorios")
    private List<Player> players;

}