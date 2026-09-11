package com.ahmedsamir.urlshortener.service;

import com.ahmedsamir.urlshortener.config.AppProperties;
import com.ahmedsamir.urlshortener.dto.CreateUrlRequest;
import com.ahmedsamir.urlshortener.dto.CreateUrlResponse;
import com.ahmedsamir.urlshortener.entity.Url;
import com.ahmedsamir.urlshortener.exception.CacheOperationException;
import com.ahmedsamir.urlshortener.exception.ShortCodeNotFoundException;
import com.ahmedsamir.urlshortener.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {
    @Mock
    private UrlCacheService urlCacheService;

    @Mock
    private AppProperties appProperties;

    @Mock
    private UrlPersistenceService urlPersistenceService;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private ShortCodeGenerator shortCodeGenerator;

    @InjectMocks
    private UrlServiceImpl urlService;

    @Test
    void shouldCreateShortUrl() {

        CreateUrlRequest request =
                new CreateUrlRequest("https://www.google.com");

        when(appProperties.baseUrl())
                .thenReturn("http://localhost:8080");

        when(shortCodeGenerator.generate())
                .thenReturn("aB91xK2");

        Url savedUrl =
                new Url("aB91xK2", "https://www.google.com");

        when(urlPersistenceService.save(any(Url.class)))
                .thenReturn(savedUrl);

        CreateUrlResponse response =
                urlService.createShortUrl(request);

        assertThat(response.shortCode())
                .isEqualTo("aB91xK2");

        assertThat(response.shortUrl())
                .isEqualTo("http://localhost:8080/aB91xK2");

        verify(shortCodeGenerator).generate();

        verify(urlPersistenceService).save(any(Url.class));
    }

    @Test
    void shouldReturnOriginalUrl() {
        String shortCode = "abc1234";
        String originalUrl = "https://www.google.com";

        Url url = new Url(shortCode, originalUrl);

        when(urlCacheService.get(shortCode))
                .thenReturn(null);

        when(urlRepository.findByShortCode(shortCode))
                .thenReturn(Optional.of(url));

        String result = urlService.getOriginalUrl(shortCode);

        assertThat(result).isEqualTo(originalUrl);

        verify(urlCacheService).get(shortCode);
        verify(urlRepository).findByShortCode(shortCode);
        verify(urlCacheService).put(shortCode, originalUrl);
    }

    @Test
    void shouldThrowExceptionWhenShortCodeDoesNotExist() {
        String shortCode = "abc1234";

        when(urlCacheService.get(shortCode))
                .thenReturn(null);

        when(urlRepository.findByShortCode(shortCode))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> urlService.getOriginalUrl(shortCode))
                .isInstanceOf(ShortCodeNotFoundException.class);

        verify(urlCacheService).get(shortCode);
        verify(urlRepository).findByShortCode(shortCode);
        verify(urlCacheService, never()).put(anyString(), anyString());
    }

    @Test
    void shouldRetryWhenShortCodeCollides() {

        CreateUrlRequest request =
                new CreateUrlRequest("https://www.google.com");

        when(shortCodeGenerator.generate())
                .thenReturn("ABC1234")
                .thenReturn("XYZ7890");

        Url secondUrl =
                new Url("XYZ7890", "https://www.google.com");

        when(urlPersistenceService.save(any(Url.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate key"))
                .thenReturn(secondUrl);
        when(appProperties.baseUrl())
                .thenReturn("http://localhost:8080");

        CreateUrlResponse response =
                urlService.createShortUrl(request);

        assertThat(response.shortCode())
                .isEqualTo("XYZ7890");

        assertThat(response.shortUrl())
                .isEqualTo("http://localhost:8080/XYZ7890");

        verify(shortCodeGenerator, times(2))
                .generate();

        verify(urlPersistenceService, times(2))
                .save(any(Url.class));
    }

    @Test
    void shouldFailAfterMaximumRetryAttempts() {

        CreateUrlRequest request =
                new CreateUrlRequest("https://www.google.com");

        when(shortCodeGenerator.generate())
                .thenReturn("ABC1234")
                .thenReturn("DEF5678")
                .thenReturn("XYZ7890");

        when(urlPersistenceService.save(any(Url.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate key"));

        assertThatThrownBy(() ->
                urlService.createShortUrl(request)
        )
                .isInstanceOf(DataIntegrityViolationException.class);

        verify(shortCodeGenerator, times(3))
                .generate();

        verify(urlPersistenceService, times(3))
                .save(any(Url.class));
    }
    @Test
    void shouldReturnOriginalUrlFromCache() {
        String shortCode = "abc1234";
        String originalUrl = "https://www.google.com";

        when(urlCacheService.get(shortCode))
                .thenReturn(originalUrl);

        String result = urlService.getOriginalUrl(shortCode);

        assertThat(result).isEqualTo(originalUrl);

        verify(urlCacheService).get(shortCode);
        verifyNoInteractions(urlRepository);
    }
    @Test
    void shouldFallbackToDatabaseWhenCacheGetFails() {
        String shortCode = "abc1234";
        String originalUrl = "https://www.google.com";

        Url url = new Url(shortCode, originalUrl);

        when(urlCacheService.get(shortCode)).thenThrow(new CacheOperationException("Failed to read URL from cache", new RuntimeException()));

        when(urlRepository.findByShortCode(shortCode))
                .thenReturn(Optional.of(url));

        String result = urlService.getOriginalUrl(shortCode);

        assertThat(result).isEqualTo(originalUrl);

        verify(urlCacheService).get(shortCode);
        verify(urlRepository).findByShortCode(shortCode);
    }
    @Test
    void shouldReturnUrlWhenCachePutFails() {
        String shortCode = "abc1234";
        String originalUrl = "https://www.google.com";

        Url url = new Url(shortCode, originalUrl);

        when(urlCacheService.get(shortCode))
                .thenReturn(null);

        when(urlRepository.findByShortCode(shortCode))
                .thenReturn(Optional.of(url));

        doThrow(new CacheOperationException("Failed to write URL to cache",
                new RuntimeException())).when(urlCacheService).put(shortCode, originalUrl);
        String result = urlService.getOriginalUrl(shortCode);

        assertThat(result).isEqualTo(originalUrl);

        verify(urlRepository).findByShortCode(shortCode);
        verify(urlCacheService).put(shortCode, originalUrl);
    }
}