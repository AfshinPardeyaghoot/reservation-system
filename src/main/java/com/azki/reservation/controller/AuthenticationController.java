package com.azki.reservation.controller;

import com.azki.reservation.dto.http.HttpResponse;
import com.azki.reservation.dto.auth.LoginRequestDto;
import com.azki.reservation.dto.auth.LoginResponseDto;
import com.azki.reservation.dto.auth.SignupRequestDto;
import com.azki.reservation.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@Tag(name = "Authentication", description = "Endpoints for user signup and login")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    @Operation(
            summary = "User Login",
            description = "Authenticates a user with username and password, and returns a JWT token upon success."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Login successful, JWT token returned",
                    content = @Content(
                            schema = @Schema(implementation = HttpResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\": \"Login successful\", \"code\": 200, \"data\": {\"token\": \"jwt-token-here\"}}")
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials",
                    content = @Content(
                            schema = @Schema(implementation = HttpResponse.class),
                            examples = @ExampleObject(value = "{\"success\": false, \"message\": \"Invalid credentials\", \"code\": 401, \"data\": null}")
                    )
            )
    })
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        String token = authenticationService.authenticateUser(
                loginRequest.username(),
                loginRequest.password()
        );

        return ResponseEntity.ok(new HttpResponse<>(new LoginResponseDto(token)));
    }

    @PostMapping("/signup")
    @Operation(
            summary = "User Signup",
            description = "Registers a new user and returns a JWT token for immediate login."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "User created successfully, JWT token returned",
                    content = @Content(
                            schema = @Schema(implementation = HttpResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\": \"User created successfully\", \"code\": 201, \"data\": {\"token\": \"jwt-token-here\"}}")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid data",
                    content = @Content(
                            schema = @Schema(implementation = HttpResponse.class),
                            examples = @ExampleObject(value = "{\"success\": false, \"message\": \"Invalid data\", \"code\": 400, \"data\": null}")
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Username already exists",
                    content = @Content(
                            schema = @Schema(implementation = HttpResponse.class),
                            examples = @ExampleObject(value = "{\"success\": false, \"message\": \"Username already exists\", \"code\": 409, \"data\": null}")
                    )
            )
    })
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequestDto signupRequest) {
        authenticationService.signup(signupRequest);

        String token = authenticationService.authenticateUser(
                signupRequest.username(),
                signupRequest.password()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(new HttpResponse<>(new LoginResponseDto(token)));
    }
}
