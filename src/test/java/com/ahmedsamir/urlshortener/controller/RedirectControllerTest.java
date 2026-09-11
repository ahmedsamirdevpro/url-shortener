package com.ahmedsamir.urlshortener.controller;

import com.ahmedsamir.urlshortener.exception.ShortCodeNotFoundException;
import com.ahmedsamir.urlshortener.service.UrlService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RedirectController.class)
class RedirectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UrlService urlService;

    @Test
    void shouldRedirectToOriginalUrl() throws Exception {

        when(urlService.getOriginalUrl("aB91xK2"))
                .thenReturn("https://www.google.com");

        mockMvc.perform(get("/aB91xK2"))
                .andExpect(status().isFound())
                .andExpect(header().string(
                        "Location",
                        "https://www.google.com"
                ));
    }

    @Test
    void shouldReturnNotFoundWhenShortCodeDoesNotExist() throws Exception {

        when(urlService.getOriginalUrl("doesNotExist"))
                .thenThrow(new ShortCodeNotFoundException("doesNotExist"));

        mockMvc.perform(get("/doesNotExist"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));
    }
}