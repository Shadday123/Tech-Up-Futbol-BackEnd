package com.techcup.techcup_futbol.Controller.dto;

import java.time.LocalDateTime;

public record ImportantDateDTO(
        String description,
        LocalDateTime date
) {}
