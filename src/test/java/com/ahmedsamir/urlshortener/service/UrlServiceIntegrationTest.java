package com.ahmedsamir.urlshortener.service;

import com.ahmedsamir.urlshortener.dto.CreateUrlRequest;
import com.ahmedsamir.urlshortener.dto.CreateUrlResponse;
import com.ahmedsamir.urlshortener.entity.Url;
import com.ahmedsamir.urlshortener.repository.UrlRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
class UrlServiceIntegrationTest {

    @Autowired
    private UrlService urlService;

    @Autowired
    private UrlRepository urlRepository;

    @MockitoBean
    private ShortCodeGenerator shortCodeGenerator;

    @AfterEach
    void cleanup() {
        urlRepository.findByShortCode("ABC1234")
                .ifPresent(urlRepository::delete);

        urlRepository.findByShortCode("XYZ7890")
                .ifPresent(urlRepository::delete);
    }

    @Test
    void shouldRetryWhenShortCodeCollidesWithExistingUrl() {

        // Existing URL in the real PostgreSQL database
        Url existingUrl =
                new Url(
                        "ABC1234",
                        "https://existing.com"
                );

        urlRepository.save(existingUrl);

        // First generated code collides,
        // second generated code is available.
        when(shortCodeGenerator.generate())
                .thenReturn("ABC1234")
                .thenReturn("XYZ7890");

        CreateUrlRequest request =
                new CreateUrlRequest(
                        "https://www.google.com"
                );

        CreateUrlResponse response =
                urlService.createShortUrl(request);

        assertThat(response.shortCode())
                .isEqualTo("XYZ7890");

        assertThat(response.shortUrl())
                .isEqualTo("http://localhost:8080/XYZ7890");

        assertThat(
                urlRepository.findByShortCode("XYZ7890")
        ).isPresent();

        assertThat(
                urlRepository.findByShortCode("XYZ7890")
                        .get()
                        .getOriginalUrl()
        ).isEqualTo("https://www.google.com");
    }
}