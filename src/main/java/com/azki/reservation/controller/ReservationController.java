package com.azki.reservation.controller;

import com.azki.reservation.dto.ReservationRequestDto;
import com.azki.reservation.dto.http.HttpResponse;
import com.azki.reservation.model.Reservation;
import com.azki.reservation.service.ReservationService;
import com.azki.reservation.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/reservation")
@AllArgsConstructor
@Tag(name = "Reservations", description = "Endpoints for creating and cancelling reservations")
public class ReservationController {

    private final UserService userService;
    private final ReservationService reservationService;

    @PostMapping
    @Operation(
            summary = "Reserve a time slot",
            description = "Creates a reservation for the logged-in user for a specific time slot. This operation is atomic to handle concurrent requests."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Slot reserved successfully",
                    content = @Content(schema = @Schema(implementation = HttpResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request or slot is already reserved",
                    content = @Content(
                            schema = @Schema(implementation = HttpResponse.class),
                            examples = @ExampleObject(value = "{\"success\": false, \"message\": \"Invalid request or slot is already reserved\", \"code\": 400, \"data\": null}")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Slot not found",
                    content = @Content(
                            schema = @Schema(implementation = HttpResponse.class),
                            examples = @ExampleObject(value = "{\"success\": false, \"message\": \"Slot not found\", \"code\": 404, \"data\": null}")
                    )
            )
    })
    public ResponseEntity<HttpResponse<Reservation>> reserve(@Valid @RequestBody ReservationRequestDto dto,
                                                             @Parameter(hidden = true) Principal principal) {

        var user = userService.findByUsername(principal.getName());
        var reservation = reservationService.reserve(dto, user);
        return ResponseEntity.ok(new HttpResponse<>(reservation));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Cancel a reservation",
            description = "Allows a user to cancel their own reservation by its ID. The slot will become available again."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Reservation cancelled successfully",
                    content = @Content(schema = @Schema(implementation = HttpResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Reservation has already been cancelled",
                    content = @Content(
                            schema = @Schema(implementation = HttpResponse.class),
                            examples = @ExampleObject(value = "{\"success\": false, \"message\": \"Reservation has already been cancelled\", \"code\": 400, \"data\": null}")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Reservation not found for this user",
                    content = @Content(
                            schema = @Schema(implementation = HttpResponse.class),
                            examples = @ExampleObject(value = "{\"success\": false, \"message\": \"Reservation not found for this user\", \"code\": 404, \"data\": null}")
                    )
            )
    })
    public ResponseEntity<HttpResponse<Void>> cancel(@PathVariable Long id,
                                                     @Parameter(hidden = true) Principal principal) {

        reservationService.cancel(id, principal.getName());
        return ResponseEntity.ok(new HttpResponse<>(null));
    }

}
