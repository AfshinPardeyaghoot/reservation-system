package com.azki.reservation.dto;

import java.time.LocalDateTime;

public record SlotDto(long id, LocalDateTime startTime, LocalDateTime endTime) {
}
