package com.techcup.techcup_futbol.util;
import lombok.Data;

@Data
public class PlayerDTO {

    private Long id;
    private String nombre;
    private String correo;
    private String foto;
    private boolean disponible;

}