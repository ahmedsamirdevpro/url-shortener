package com.ahmedsamir.urlshortener.controller;

import com.ahmedsamir.urlshortener.dto.CreateUrlResponse;
import com.ahmedsamir.urlshortener.service.UrlService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UrlController.class)
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UrlService urlService;

    @Test
    void shouldCreateShortUrl() throws Exception {

        when(urlService.createShortUrl(any()))
                .thenReturn(
                        new CreateUrlResponse(
                                "aB91xK2",
                                "http://localhost:8080/aB91xK2"
                        )
                );

        mockMvc.perform(
                        post("/api/v1/urls")
                                .contentType(APPLICATION_JSON)
                                .content("""
                                        {
                                            "originalUrl": "https://www.google.com"
                                        }
                                        """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortCode")
                        .value("aB91xK2"))
                .andExpect(jsonPath("$.shortUrl")
                        .value("http://localhost:8080/aB91xK2"));
    }

    @Test
    void shouldReturnBadRequestWhenOriginalUrlIsBlank()
            throws Exception {

        mockMvc.perform(
                        post("/api/v1/urls")
                                .contentType(APPLICATION_JSON)
                                .content("""
                                        {
                                            "originalUrl": ""
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());
    }
}