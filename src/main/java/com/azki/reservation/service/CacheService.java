package com.azki.reservation.service;

import com.azki.reservation.config.redis.RedisConfig;
import com.azki.reservation.model.Slot;
import com.azki.reservation.repository.SlotRepository;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@AllArgsConstructor
public class CachService {

    public static final String KEY = RedisConfig.AVAILABLE_SLOTS_ZSET;
    private final SlotRepository slotRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Transactional(readOnly = true)
    public void populateCache() {
        List<Slot> available = slotRepository.findAvailable(
                Pageable.unpaged(), LocalDateTime.now()).getContent();

        redisTemplate.delete(KEY);

        redisTemplate.executePipelined((RedisCallback<Object>) cnn -> {
            available.forEach(s ->
                    cnn.zAdd(KEY.getBytes(StandardCharsets.UTF_8),
                            s.getStartTime().toEpochSecond(ZoneOffset.UTC),
                            s.getId().toString().getBytes(StandardCharsets.UTF_8)));

            return null;
        });
    }

}
