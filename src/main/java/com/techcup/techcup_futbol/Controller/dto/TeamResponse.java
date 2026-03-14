package com.techcup.techcup_futbol.Controller.dto;


import java.util.List;

public record TeamResponse(
        String id,
        String name,
        String logoUrl,
        String status, // Ejemplo: "PENDING", "APPROVED"
        String captainName,
        List<String> playerNames, // Lista simple de nombres de los integrantes
        int totalPlayers // Para validar fácilmente si hay entre 7 y 12
) {}
