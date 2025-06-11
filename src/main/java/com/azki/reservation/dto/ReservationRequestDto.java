package com.azki.reservation.dto;

import jakarta.validation.constraints.NotBlank;

public record ReservationRequestDto(@NotBlank(message = "requestId is required") Long requestId) {
}
