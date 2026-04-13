package com.techcup.techcup_futbol.exception;

package com.techcup.techcup_futbol.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ── TeamException ──────────────────────────────────────────────────────

    @ExceptionHandler(TeamException.class)
    public ResponseEntity<Map<String, Object>> handleTeamException(TeamException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getField(), ex.getMessage());
    }

    // ── PlayerException ────────────────────────────────────────────────────

    @ExceptionHandler(PlayerException.class)
    public ResponseEntity<Map<String, Object>> handlePlayerException(PlayerException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getField(), ex.getMessage());
    }

    // ── TournamentException ────────────────────────────────────────────────

    @ExceptionHandler(TournamentException.class)
    public ResponseEntity<Map<String, Object>> handleTournamentException(TournamentException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getField(), ex.getMessage());
    }

    // ── BracketException ───────────────────────────────────────────────────

    @ExceptionHandler(BracketException.class)
    public ResponseEntity<Map<String, Object>> handleBracketException(BracketException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getField(), ex.getMessage());
    }

    // ── LineupException ────────────────────────────────────────────────────

    @ExceptionHandler(LineupException.class)
    public ResponseEntity<Map<String, Object>> handleLineupException(LineupException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getField(), ex.getMessage());
    }

    // ── MatchException ─────────────────────────────────────────────────────

    @ExceptionHandler(MatchException.class)
    public ResponseEntity<Map<String, Object>> handleMatchException(MatchException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getField(), ex.getMessage());
    }

    // ── PaymentException ───────────────────────────────────────────────────

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<Map<String, Object>> handlePaymentException(PaymentException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getField(), ex.getMessage());
    }

    // ── RefereeException ───────────────────────────────────────────────────

    @ExceptionHandler(RefereeException.class)
    public ResponseEntity<Map<String, Object>> handleRefereeException(RefereeException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getField(), ex.getMessage());
    }

    // ── @Valid — errores de validación de campos ───────────────────────────

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Error de validación");
        body.put("fields", fieldErrors);
        return ResponseEntity.badRequest().body(body);
    }

    // ── IllegalArgumentException (ej: tipo de jugador inválido) ───────────

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, null, ex.getMessage());
    }

    // ── Fallback genérico ──────────────────────────────────────────────────

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, null,
                "Error interno del servidor: " + ex.getMessage());
    }

    // ── Helper ─────────────────────────────────────────────────────────────

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status,
                                                               String field,
                                                               String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", message);
        if (field != null) {
            body.put("field", field);
        }
        return ResponseEntity.status(status).body(body);
    }
}
