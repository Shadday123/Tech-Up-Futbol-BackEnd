package com.techcup.techcup_futbol.exception;

import com.techcup.techcup_futbol.core.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PlayerException.class)
    public ErrorResponse handlePlayerException(PlayerException ex, HttpServletRequest request) {
        HttpStatus status = resolveStatus(ex.getMessage());
        return buildResponse(status, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(TeamException.class)
    public ErrorResponse handleTeamException(TeamException ex, HttpServletRequest request) {
        HttpStatus status = resolveStatus(ex.getMessage());
        return buildResponse(status, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(TournamentException.class)
    public ErrorResponse handleTournamentException(TournamentException ex, HttpServletRequest request) {
        HttpStatus status = resolveStatus(ex.getMessage());
        return buildResponse(status, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(RuntimeException.class)
    public ErrorResponse handleRuntimeException(RuntimeException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ErrorResponse handleException(Exception ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor", request.getRequestURI());
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
        if (message != null && (message.contains("No existe") || message.contains("No se encontró"))) {
            return HttpStatus.NOT_FOUND;
        }
        return HttpStatus.BAD_REQUEST;
    }
}












