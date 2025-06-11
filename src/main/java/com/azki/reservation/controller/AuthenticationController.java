package com.azki.reservation.controller;

import com.azki.reservation.dto.http.HttpResponse;
import com.azki.reservation.dto.auth.LoginRequestDto;
import com.azki.reservation.dto.auth.LoginResponseDto;
import com.azki.reservation.dto.auth.SignupRequestDto;
import com.azki.reservation.service.AuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/authentication")
@AllArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequest) {
        String token = authenticationService.authenticateUser(
                loginRequest.username(),
                loginRequest.password()
        );

        return ResponseEntity.ok(new HttpResponse<>(new LoginResponseDto(token)));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequestDto signupRequest) {
        authenticationService.signup(signupRequest);

        String token = authenticationService.authenticateUser(
                signupRequest.username(),
                signupRequest.password()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(new HttpResponse<>(new LoginResponseDto(token)));
    }
}
