package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.core.security.JwtAuthEntryPoint;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthEntryPointTest {

    @InjectMocks
    private JwtAuthEntryPoint entryPoint;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private AuthenticationException authException;

    private StringWriter responseBody;
    private PrintWriter writer;

    @BeforeEach
    void setUp() throws IOException {
        responseBody = new StringWriter();
        writer = new PrintWriter(responseBody);
        when(response.getWriter()).thenReturn(writer);
    }

    @Test
    void commence_withoutJwtError_returnsRequiereAutenticacion() throws IOException {
        when(request.getAttribute("jwt.error")).thenReturn(null);

        entryPoint.commence(request, response, authException);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType("application/json;charset=UTF-8");
        String body = responseBody.toString();
        assertTrue(body.contains("Se requiere autenticación"));
        assertTrue(body.contains("401"));
    }

    @Test
    void commence_withJwtError_returnsSpecificMessage() throws IOException {
        when(request.getAttribute("jwt.error")).thenReturn("Token expirado");

        entryPoint.commence(request, response, authException);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        String body = responseBody.toString();
        assertTrue(body.contains("Token expirado"));
    }

    @Test
    void commence_withTokenInvalidoError_returnsMessage() throws IOException {
        when(request.getAttribute("jwt.error")).thenReturn("Token inválido");

        entryPoint.commence(request, response, authException);

        String body = responseBody.toString();
        assertTrue(body.contains("Token inválido"));
    }
}
