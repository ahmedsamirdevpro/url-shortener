package com.ahmedsamir.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUrlRequest(
        @NotBlank(message = "Original URL Must Not Be Blank")
        @Size(max = 2048, message = "Original Url Too Long")
        String originalUrl) {
}
