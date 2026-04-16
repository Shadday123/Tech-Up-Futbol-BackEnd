package com.techcup.techcup_futbol.controller;

import com.techcup.techcup_futbol.core.model.SystemRole;
import com.techcup.techcup_futbol.core.security.JwtUtil;
import com.techcup.techcup_futbol.persistence.entity.UserEntity;
import com.techcup.techcup_futbol.persistence.repository.PlayerRepository;
import com.techcup.techcup_futbol.persistence.repository.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.techcup.techcup_futbol.core.util.Base64Util;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String ORG_TOKEN = "TECHCUP-ORG-2025";

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PlayerRepository playerRepository;

    public AuthController(AuthenticationManager authManager,
                          JwtUtil jwtUtil,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          PlayerRepository playerRepository) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.playerRepository = playerRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {
        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(), request.password()));

            UserDetails user = (UserDetails) auth.getPrincipal();
            String token = jwtUtil.generateToken(user);
            String rol = user.getAuthorities().stream()
                    .findFirst()
                    .map(Object::toString)
                    .orElse("ROLE_JUGADOR");

            String userId = playerRepository.findByEmailIgnoreCase(user.getUsername())
                    .map(p -> p.getId())
                    .orElseGet(() -> userRepository.findByEmail(user.getUsername())
                            .map(u -> u.getId().toString())
                            .orElse(""));

            return ResponseEntity.ok(
                    new LoginResponse(token, user.getUsername(), rol, "Login exitoso", userId));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401)
                    .body(new LoginResponse(null, null, null, "Credenciales incorrectas", null));
        }
    }

    @PostMapping("/registro/organizador")
    public ResponseEntity<LoginResponse> registrarOrganizador(
            @Valid @RequestBody OrganizerRegistrationRequest request) {

        if (!ORG_TOKEN.equals(request.authToken())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new LoginResponse(null, null, null, "Token de autorización inválido", null));
        }

        if (userRepository.findByEmail(request.email()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new LoginResponse(null, null, null, "El correo '" + request.email() + "' ya está registrado. Usa uno diferente.", null));
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(request.email());
        userEntity.setPasswordHash(Base64Util.encode(passwordEncoder.encode(request.password())));
        userEntity.setRole(SystemRole.ORGANIZADOR);
        userRepository.save(userEntity);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new LoginResponse(null, request.email(), "ORGANIZADOR", "Organizador registrado exitosamente", userEntity.getId().toString()));
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
            String mensaje,
            String userId
    ) {}

    public record OrganizerRegistrationRequest(
            @NotBlank(message = "El nombre es obligatorio")
            String fullname,

            @NotBlank(message = "El correo es obligatorio")
            @Email(message = "Formato de correo inválido")
            String email,

            @NotBlank(message = "La contraseña es obligatoria")
            @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
            @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d).+$",
                    message = "La contraseña debe tener al menos una mayúscula y un número")
            String password,

            @NotBlank(message = "El token de autorización es obligatorio")
            String authToken
    ) {}
}
