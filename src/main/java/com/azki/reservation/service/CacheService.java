package com.azki.reservation.service;

import com.azki.reservation.config.redis.RedisConfig;
import com.azki.reservation.model.Slot;
import com.azki.reservation.repository.SlotRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class CacheService {

    public static final String KEY = RedisConfig.AVAILABLE_SLOTS_ZSET;
    private final SlotRepository slotRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Transactional(readOnly = true)
    public void populateCache() {
        redisTemplate.delete(KEY);

        int pageSize = 100_000;
        int page = 0;
        boolean hasMore = true;

        while (true) {
            Page<Slot> pageResult = slotRepository.findAvailable(PageRequest.of(page, pageSize), LocalDateTime.now());
            List<Slot> available = pageResult.getContent();
            log.info("Load available slots to cache (page {}):{}", page, available.size());

            if (available.isEmpty()) {
                hasMore = false;
                break;
            }

            Set<ZSetOperations.TypedTuple<Object>> tuples = available.stream()
                    .map(s -> ZSetOperations.TypedTuple.of(
                            (Object) s.getId().toString(),
                            (double) s.getStartTime().toEpochSecond(ZoneOffset.UTC)
                    ))
                    .collect(Collectors.toSet());


            if (!tuples.isEmpty()) {
                redisTemplate.opsForZSet().add(KEY, tuples);
            }

            if (!pageResult.hasNext()) {
                break;
            }

            page++;
        }

        log.info("Cache population completed.");
    }


    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional(readOnly = false)
    public void cleanupExpiredSlots() {
        long expiryThreshold = LocalDateTime.now().minusDays(1).toEpochSecond(ZoneOffset.UTC);

        Long removedCount = redisTemplate.opsForZSet().removeRangeByScore(KEY, 0, expiryThreshold);

        log.info("Cleaned up {} expired slots from cache.", removedCount != null ? removedCount : 0);
    }

}
