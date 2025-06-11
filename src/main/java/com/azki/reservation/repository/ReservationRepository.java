package com.azki.reservation.repository;

import com.azki.reservation.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

    Optional<Reservation> findByIdAndUserId(Long id, Long userId);

}
