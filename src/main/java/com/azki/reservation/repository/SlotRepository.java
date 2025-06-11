package com.azki.reservation.repository;

import com.azki.reservation.model.Slot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface SlotRepository extends JpaRepository<Slot, Long> {


    @Query("""
            select s from Slot s
            where s.isReserved = false
              and s.startTime > :now
            order by s.startTime
            """)
    Page<Slot> findAvailable(Pageable pageable, LocalDateTime now);

}
