package com.ahmedsamir.urlshortener.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Standard API error response")
public record ErrorResponse( @Schema(description = "Timestamp when the error occurred", example = "2026-09-12T12:30:00Z")Instant timestamp,
                             @Schema(description = "HTTP status code", example = "404")int status,
                             @Schema(description = "HTTP error description", example = "Not Found")String error,
                             @Schema(description = "Detailed error message", example = "Short code not found: ABC1234")String message) {
}
