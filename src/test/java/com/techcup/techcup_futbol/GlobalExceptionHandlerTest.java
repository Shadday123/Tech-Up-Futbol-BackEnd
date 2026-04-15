package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.core.exception.BracketException;
import com.techcup.techcup_futbol.core.exception.PaymentException;
import com.techcup.techcup_futbol.core.exception.MatchException;
import com.techcup.techcup_futbol.core.exception.PlayerException;
import com.techcup.techcup_futbol.core.exception.TeamException;
import com.techcup.techcup_futbol.core.exception.TournamentException;
import com.techcup.techcup_futbol.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleTeamException_returnsBadRequest() {
        TeamException ex = new TeamException("teamName", "Nombre duplicado");
        ResponseEntity<Map<String, Object>> response = handler.handleTeamException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("teamName", response.getBody().get("field"));
        assertEquals("Nombre duplicado", response.getBody().get("error"));
    }

    @Test
    void handlePlayerException_returnsBadRequest() {
        PlayerException ex = new PlayerException("email", "Email inválido");
        ResponseEntity<Map<String, Object>> response = handler.handlePlayerException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("email", response.getBody().get("field"));
    }

    @Test
    void handleTournamentException_returnsBadRequest() {
        TournamentException ex = new TournamentException("name", "Nombre requerido");
        ResponseEntity<Map<String, Object>> response = handler.handleTournamentException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("name", response.getBody().get("field"));
    }

    @Test
    void handleMatchException_returnsBadRequest() {
        MatchException ex = new MatchException("matchId", "Partido no encontrado");
        ResponseEntity<Map<String, Object>> response = handler.handleMatchException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("matchId", response.getBody().get("field"));
    }


    @Test
    void handleIllegalArgument_returnsBadRequest() {
        IllegalArgumentException ex = new IllegalArgumentException("Argumento inválido");
        ResponseEntity<Map<String, Object>> response = handler.handleIllegalArgument(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody().get("field"));
    }

    @Test
    void handleGeneralException_returnsServerError() {
        Exception ex = new RuntimeException("Error inesperado");
        ResponseEntity<Map<String, Object>> response = handler.handleGeneral(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        String errorMsg = (String) response.getBody().get("error");
        assertTrue(errorMsg.contains("Error inesperado"));
    }

    @Test
    void response_includesTimestamp() {
        TeamException ex = new TeamException("field", "message");
        ResponseEntity<Map<String, Object>> response = handler.handleTeamException(ex);

        String timestamp = (String) response.getBody().get("timestamp");
        assertTrue(LocalDateTime.parse(timestamp).isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void handleBracketException_returnsBadRequest() {
        BracketException ex = new BracketException("bracket", "Bracket inválido");
        ResponseEntity<Map<String, Object>> response = handler.handleBracketException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("bracket", response.getBody().get("field"));
    }

    @Test
    void handlePaymentException_returnsBadRequest() {
        PaymentException ex = new PaymentException("payment", "Pago inválido");
        ResponseEntity<Map<String, Object>> response = handler.handlePaymentException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("payment", response.getBody().get("field"));
    }
}