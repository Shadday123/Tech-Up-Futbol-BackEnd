package com.techcup.techcup_futbol.exception;

import com.techcup.techcup_futbol.core.exception.TeamException;
import com.techcup.techcup_futbol.core.exception.PlayerException;
import com.techcup.techcup_futbol.core.exception.TournamentException;
import com.techcup.techcup_futbol.core.exception.BracketException;
import com.techcup.techcup_futbol.core.exception.LineupException;
import com.techcup.techcup_futbol.core.exception.MatchException;
import com.techcup.techcup_futbol.core.exception.PaymentException;
import com.techcup.techcup_futbol.core.exception.RefereeException;
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


    @ExceptionHandler(TeamException.class)
    public ResponseEntity<Map<String, Object>> handleTeamException(TeamException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getField(), ex.getMessage());
    }


    @ExceptionHandler(PlayerException.class)
    public ResponseEntity<Map<String, Object>> handlePlayerException(PlayerException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getField(), ex.getMessage());
    }


    @ExceptionHandler(TournamentException.class)
    public ResponseEntity<Map<String, Object>> handleTournamentException(TournamentException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getField(), ex.getMessage());
    }


    @ExceptionHandler(BracketException.class)
    public ResponseEntity<Map<String, Object>> handleBracketException(BracketException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getField(), ex.getMessage());
    }


    @ExceptionHandler(LineupException.class)
    public ResponseEntity<Map<String, Object>> handleLineupException(LineupException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getField(), ex.getMessage());
    }


    @ExceptionHandler(MatchException.class)
    public ResponseEntity<Map<String, Object>> handleMatchException(MatchException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getField(), ex.getMessage());
    }


    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<Map<String, Object>> handlePaymentException(PaymentException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getField(), ex.getMessage());
    }


    @ExceptionHandler(RefereeException.class)
    public ResponseEntity<Map<String, Object>> handleRefereeException(RefereeException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getField(), ex.getMessage());
    }


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


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, null, ex.getMessage());
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, null,
                "Error interno del servidor: " + ex.getMessage());
    }


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
