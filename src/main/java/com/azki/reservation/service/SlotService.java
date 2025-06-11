package com.azki.reservation.service;

import com.azki.reservation.config.redis.RedisConfig;
import com.azki.reservation.dto.SlotDto;
import com.azki.reservation.exception.NotFoundException;
import com.azki.reservation.model.Slot;
import com.azki.reservation.repository.SlotRepository;
import jakarta.annotation.PostConstruct;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class SlotService {

    private final CacheService cacheService;
    private final SlotRepository slotRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String KEY = RedisConfig.AVAILABLE_SLOTS_ZSET;

    @PostConstruct
    public void warmUpCache() {
        cacheService.populateCache();
    }

    @Transactional(readOnly = true)
    public Page<SlotDto> getAvailable(Pageable pageable) {
        long start = pageable.getOffset();
        long end = start + pageable.getPageSize() - 1;

        Set<Object> idObjs = redisTemplate.opsForZSet().range(KEY, start, end);
        if (idObjs == null || idObjs.isEmpty()) {
            cacheService.populateCache();
            idObjs = redisTemplate.opsForZSet().range(KEY, start, end);
        }

        List<Long> ids = idObjs.stream().map(o -> Long.valueOf(o.toString())).toList();
        List<SlotDto> slots = slotRepository.findAllById(ids).stream()
                .map(s -> new SlotDto(s.getId(), s.getStartTime(), s.getEndTime()))
                .sorted(Comparator.comparing(SlotDto::startTime))
                .toList();

        Long total = redisTemplate.opsForZSet().zCard(KEY);
        return new PageImpl<>(slots, pageable, total == null ? 0 : total);
    }

    public int markSlotAsReserved(long id) {
        return slotRepository.markAsReserved(id);
    }

    public Slot findById(long id) {
        return slotRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Slot not found"));
    }

}
