package com.ahmedsamir.urlshortener.service;

import com.ahmedsamir.urlshortener.entity.Url;
import com.ahmedsamir.urlshortener.repository.UrlRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UrlServiceCacheIntegrationTest {

    private static final String SHORT_CODE = "ABC1234";
    private static final String ORIGINAL_URL = "https://www.google.com";
    private static final String REDIS_KEY = "url:" + SHORT_CODE;

    @Autowired
    private UrlService urlService;

    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @AfterEach
    void cleanup() {
        redisTemplate.delete(REDIS_KEY);
        urlRepository.findByShortCode(SHORT_CODE)
                .ifPresent(url -> urlRepository.deleteById(url.getId()));
    }

    @Test
    void shouldLoadUrlFromDatabaseAndPopulateCacheOnCacheMiss() {

        Url url = new Url(SHORT_CODE, ORIGINAL_URL);
        urlRepository.save(url);

        redisTemplate.delete(REDIS_KEY);

        String result = urlService.getOriginalUrl(SHORT_CODE);

        assertThat(result)
                .isEqualTo(ORIGINAL_URL);

        String cachedValue = redisTemplate.opsForValue()
                .get(REDIS_KEY);

        assertThat(cachedValue)
                .isEqualTo(ORIGINAL_URL);
    }

    @Test
    void shouldReturnUrlFromCacheOnCacheHit() {

        Url url = new Url(SHORT_CODE, ORIGINAL_URL);
        urlRepository.save(url);

        redisTemplate.opsForValue()
                .set(REDIS_KEY, ORIGINAL_URL);

        String result = urlService.getOriginalUrl(SHORT_CODE);

        assertThat(result)
                .isEqualTo(ORIGINAL_URL);
    }
    @Test
    void shouldStoreUrlWithOneHourTtl() {
        Url url = new Url(SHORT_CODE, ORIGINAL_URL);
        urlRepository.save(url);

        redisTemplate.delete(REDIS_KEY);

        urlService.getOriginalUrl(SHORT_CODE);

        Long ttl = redisTemplate.getExpire(REDIS_KEY);

        assertThat(ttl).isBetween(1L, 3600L);
    }
}