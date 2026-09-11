package com.ahmedsamir.urlshortener.service;

import com.ahmedsamir.urlshortener.exception.CacheOperationException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisUrlCacheService implements UrlCacheService {
    private static final String KEY_PREFIX = "url:";
    private static final Duration TTL = Duration.ofHours(1);
    private final StringRedisTemplate redisTemplate;

    public RedisUrlCacheService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String get(String shortCode) {
        try {
            return redisTemplate.opsForValue().get(buildKey(shortCode));
        } catch (RuntimeException exception) {
            throw new CacheOperationException("Failed to read URL from cache", exception);
        }
    }
    @Override
    public void put(String shortCode, String originalUrl) {
        try {
            redisTemplate.opsForValue().set(buildKey(shortCode), originalUrl, TTL);
        } catch (RuntimeException exception) {
            throw new CacheOperationException("Failed to write URL to cache", exception);
        }
    }
    private String buildKey(String shortCode) {
        return KEY_PREFIX + shortCode;
    }
}