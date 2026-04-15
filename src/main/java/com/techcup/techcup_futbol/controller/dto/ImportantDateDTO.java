package com.techcup.techcup_futbol.controller.dto;

import java.time.LocalDateTime;

public record ImportantDateDTO(
        String description,
        LocalDateTime date
) {}
