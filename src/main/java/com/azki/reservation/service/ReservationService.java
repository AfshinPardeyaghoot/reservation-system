package com.azki.reservation.service;

import com.azki.reservation.config.redis.RedisConfig;
import com.azki.reservation.dto.ReservationRequestDto;
import com.azki.reservation.exception.HttpException;
import com.azki.reservation.exception.InternalServerException;
import com.azki.reservation.exception.NotFoundException;
import com.azki.reservation.model.Reservation;
import com.azki.reservation.model.User;
import com.azki.reservation.repository.ReservationRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@AllArgsConstructor
public class ReservationService {

    private final UserService userService;
    private final SlotService slotService;
    private final ReservationRepository reservationRepository;
    private final DefaultRedisScript<Long> reserveSlotScript;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String KEY = RedisConfig.AVAILABLE_SLOTS_ZSET;

    @Transactional
    public Reservation reserve(ReservationRequestDto dto, User user) {
        var slotId = dto.requestId();

        var luaResult = redisTemplate.execute(
                reserveSlotScript,
                List.of(KEY),
                slotId.toString());

        if (luaResult == 0) {
            throw new HttpException("Slot already reserved.", HttpStatus.BAD_REQUEST);
        }

        var updated = slotService.markSlotAsReserved(slotId);
        if (updated == 0) {
            rollbackRedis(slotId);
            throw new InternalServerException("500", "Slot reservation failed in DB.");
        }

        return save(Reservation.builder()
                .user(user)
                .slot(slotService.findById(slotId))
                .reservedAt(LocalDateTime.now())
                .status(Reservation.Status.ACTIVE)
                .build());
    }

    @Transactional
    public void cancel(Long reservationId, String username) {
        var userId = userService.findByUsername(username).getId();
        var reservation = findByIdAndUserId(reservationId, userId);

        if (reservation.getStatus() == Reservation.Status.CANCELLED)
            throw new HttpException("Already cancelled", HttpStatus.BAD_REQUEST);

        reservation.setStatus(Reservation.Status.CANCELLED);
        reservation.getSlot().setReserved(false);
        reservationRepository.save(reservation);

        var slot = reservation.getSlot();
        redisTemplate.opsForZSet().add(KEY,
                slot.getId().toString(),
                slot.getStartTime().toEpochSecond(ZoneOffset.UTC));
    }

    public Reservation findByIdAndUserId(Long id, Long userId) {
        return reservationRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NotFoundException("Reservation not found"));
    }

    public Reservation save(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    private void rollbackRedis(Long slotId) {
        var slot = slotService.findById(slotId);

        if (slot != null) {
            redisTemplate.opsForZSet().add(KEY,
                    slotId.toString(),
                    slot.getStartTime().toEpochSecond(ZoneOffset.UTC));
        }
    }

}
