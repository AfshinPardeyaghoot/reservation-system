package com.azki.reservation.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record SignupRequestDto(
        @NotBlank(message = "Username is required") String username,
        @NotBlank(message = "Email is required") String email,
        @NotBlank(message = "Password is required") String password
) {
}
