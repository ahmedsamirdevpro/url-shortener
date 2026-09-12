package com.ahmedsamir.urlshortener.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUrlRequest(
        @Schema(description = "The original URL to be shortened", example = "https://www.example.com")
        @NotBlank(message = "Original URL Must Not Be Blank")
        @Size(max = 2048, message = "Original Url Too Long")
        String originalUrl) {
}
