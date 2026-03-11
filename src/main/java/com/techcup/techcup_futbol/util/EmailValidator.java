package com.techcup.techcup_futbol.util;


import java.util.regex.Pattern;

public class EmailValidator {

    public static boolean esCorreoValido(String correo){
        return correo.matches("^[A-Za-z0-9+_.-]+@(escuelaing\\.edu\\.co|gmail\\.com)$");
    }

}
