package com.techcup.techcup_futbol.Controller;

import com.techcup.techcup_futbol.security.JwtUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authManager,
                          JwtUtil jwtUtil) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {
        try {
            // Spring verifica email y contraseña contra UserDetailsServiceImpl
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(), request.password()));

            UserDetails user = (UserDetails) auth.getPrincipal();
            String token = jwtUtil.generateToken(user);
            String rol = user.getAuthorities().stream()
                    .findFirst()
                    .map(Object::toString)
                    .orElse("ROLE_JUGADOR");

            return ResponseEntity.ok(
                    new LoginResponse(token, user.getUsername(), rol, "Login exitoso"));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401)
                    .body(new LoginResponse(
                            null, null, null, "Credenciales incorrectas"));
        }
    }

    // DTOs internos
    public record LoginRequest(
            @NotBlank(message = "El email es obligatorio")
            @Email(message = "Formato de email inválido")
            String email,

            @NotBlank(message = "La contraseña es obligatoria")
            String password
    ) {}

    public record LoginResponse(
            String token,
            String email,
            String rol,
            String mensaje
    ) {}
}
