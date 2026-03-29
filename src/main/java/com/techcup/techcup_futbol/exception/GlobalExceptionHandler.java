package com.techcup.techcup_futbol.exception;

import com.techcup.techcup_futbol.core.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.web.bind.annotation.*;

import java.lang.MatchException;
import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PlayerException.class)
    public ErrorResponse handlePlayerException(PlayerException ex, HttpServletRequest req) {
        return buildResponse(resolveStatus(ex.getMessage()), ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(TeamException.class)
    public ErrorResponse handleTeamException(TeamException ex, HttpServletRequest req) {
        return buildResponse(resolveStatus(ex.getMessage()), ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(TournamentException.class)
    public ErrorResponse handleTournamentException(TournamentException ex, HttpServletRequest req) {
        HttpStatus status = "id".equals(ex.getField()) || "config".equals(ex.getField())
                ? HttpStatus.NOT_FOUND
                : HttpStatus.BAD_REQUEST;
        return buildResponse(status, ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(MatchException.class)
    public ErrorResponse handleMatchException(MatchException ex, HttpServletRequest req) {
        return buildResponse(resolveStatus(ex.getMessage()), ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(BracketException.class)
    public ErrorResponse handleBracketException(BracketException ex, HttpServletRequest req) {
        return buildResponse(resolveStatus(ex.getMessage()), ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(LineupException.class)
    public ErrorResponse handleLineupException(LineupException ex, HttpServletRequest req) {
        return buildResponse(resolveStatus(ex.getMessage()), ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(PaymentException.class)
    public ErrorResponse handlePaymentException(PaymentException ex, HttpServletRequest req) {
        return buildResponse(resolveStatus(ex.getMessage()), ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(RefereeException.class)
    public ErrorResponse handleRefereeException(RefereeException ex, HttpServletRequest req) {
        return buildResponse(resolveStatus(ex.getMessage()), ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ErrorResponse handleResourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest req) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ErrorResponse handleDuplicateResourceException(DuplicateResourceException ex, HttpServletRequest req) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(DatabaseException.class)
    public ErrorResponse handleDatabaseException(DatabaseException ex, HttpServletRequest req) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorResponse handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpServletRequest req) {
        return buildResponse(HttpStatus.CONFLICT, "Ya existe un recurso con esos datos", req.getRequestURI());
    }

    @ExceptionHandler(JpaSystemException.class)
    public ErrorResponse handleJpaSystemException(JpaSystemException ex, HttpServletRequest req) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno de base de datos", req.getRequestURI());
    }

    @ExceptionHandler(RuntimeException.class)
    public ErrorResponse handleRuntimeException(RuntimeException ex, HttpServletRequest req) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ErrorResponse handleException(Exception ex, HttpServletRequest req) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor", req.getRequestURI());
    }

    private ErrorResponse buildResponse(HttpStatus status, String message, String path) {
        return new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                LocalDateTime.now()
        );
    }

    private HttpStatus resolveStatus(String message) {
        if (message != null
                && (message.contains("No existe") || message.contains("No se encontró"))) {
            return HttpStatus.NOT_FOUND;
        }
        return HttpStatus.BAD_REQUEST;
    }
}
