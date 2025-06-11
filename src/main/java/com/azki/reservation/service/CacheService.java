package com.azki.reservation.service;

import com.azki.reservation.config.redis.RedisConfig;
import com.azki.reservation.model.Slot;
import com.azki.reservation.repository.SlotRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@AllArgsConstructor
public class CacheService {

    public static final String KEY = RedisConfig.AVAILABLE_SLOTS_ZSET;
    private final SlotRepository slotRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Transactional(readOnly = true)
    public void populateCache() {
        int pageSize = 100_000;
        int page = 0;
        boolean hasMore = true;

        while (hasMore) {
            Page<Slot> pageResult = slotRepository.findAvailable(PageRequest.of(page, pageSize), LocalDateTime.now());
            List<Slot> available = pageResult.getContent();
            System.out.println("Load available slots to cache (page " + page + "): " + available.size());

            if (available.isEmpty()) {
                hasMore = false;
                break;
            }

            redisTemplate.executePipelined((RedisCallback<Object>) cnn -> {
                available.forEach(s ->
                        cnn.zAdd(KEY.getBytes(StandardCharsets.UTF_8),
                                s.getStartTime().toEpochSecond(ZoneOffset.UTC),
                                s.getId().toString().getBytes(StandardCharsets.UTF_8)));
                return null;
            });

            page++;
        }
        System.out.println("Cache population completed.");
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional(readOnly = false)
    public void cleanupExpiredSlots() {
        long expiryThreshold = LocalDateTime.now().minusDays(1).toEpochSecond(ZoneOffset.UTC);

        Long removedCount = redisTemplate.opsForZSet().removeRangeByScore(KEY, 0, expiryThreshold);

        System.out.println("Cleaned up " + (removedCount != null ? removedCount : 0) + " expired slots from cache.");
    }

}
