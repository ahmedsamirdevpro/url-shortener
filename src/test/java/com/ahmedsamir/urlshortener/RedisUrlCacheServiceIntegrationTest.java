package com.ahmedsamir.urlshortener;

import com.ahmedsamir.urlshortener.service.RedisUrlCacheService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RedisUrlCacheServiceIntegrationTest {
    @Autowired
    private RedisUrlCacheService cacheService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    private static final String SHORT_CODE = "test123";
    @AfterEach
    void cleanup() {
        redisTemplate.delete("url:" + SHORT_CODE);
    }

    @Test
    void shouldPutAndGetUrl() {
        String originalUrl = "https://www.google.com";
        cacheService.put(SHORT_CODE, originalUrl);
        String result = cacheService.get(SHORT_CODE);
        assertThat(result).isEqualTo(originalUrl);
    }

    @Test
    void shouldReturnNullWhenUrlDoesNotExistInCache() {
        String result = cacheService.get(SHORT_CODE);
        assertThat(result).isNull();
    }
}