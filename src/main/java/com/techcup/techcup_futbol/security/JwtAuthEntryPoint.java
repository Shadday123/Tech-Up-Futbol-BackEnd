package com.techcup.techcup_futbol.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper mapper = new ObjectMapper();
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        // Lee el mensaje que dejó el JwtFilter en el request
        String mensaje = (String) request.getAttribute("jwt.error");

        // Si no hay mensaje específico, es que no vino token
        if (mensaje == null) {
            mensaje = "Se requiere autenticación";
        }

        mapper.writeValue(response.getWriter(),
                Map.of("status", 401, "error", mensaje));
    }
}
