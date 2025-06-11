package com.azki.reservation.controller;

import com.azki.reservation.dto.ReservationRequestDto;
import com.azki.reservation.model.Reservation;
import com.azki.reservation.service.ReservationService;
import com.azki.reservation.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/reservation")
@AllArgsConstructor
public class ReservationController {

    private final UserService userService;
    private final ReservationService reservationService;

    @PostMapping
    public Reservation reserve(@RequestBody ReservationRequestDto dto,
                               Principal principal) {

        var user = userService.findByUsername(principal.getName());
        return reservationService.reserve(dto, user);
    }

    @DeleteMapping("/{id}")
    public void cancel(@PathVariable Long id,
                       Principal principal) {

        reservationService.cancel(id, principal.getName());
    }

}
