package com.azki.reservation.dto;

import java.time.LocalDateTime;

public record ReservationResponseDto(
        Long id,
        Long slotId,
        String username,
        LocalDateTime reservedAt
) {
}
