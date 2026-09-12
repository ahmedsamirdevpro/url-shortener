package com.ahmedsamir.urlshortener.controller;

import com.ahmedsamir.urlshortener.dto.ErrorResponse;
import com.ahmedsamir.urlshortener.service.UrlService;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.net.URI;

@RestController
@Tag(name = "URL Shortener", description = "Operations for creating and resolving shortened URLs")
public class RedirectController {
    private final UrlService urlService;
    public RedirectController(UrlService urlService){
        this.urlService=urlService;
    }
    @Operation(
            summary = "Redirect to the original URL",
            description = "Resolves a short code and redirects the client to the original URL."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "302", description = "Redirect to the original URL", headers = {
                            @Header(
                                    name = "Location",
                                    description = "The original URL",
                                    schema = @Schema(
                                            type = "string",
                                            format = "uri",
                                            example = "https://www.example.com"
                                    ))}),
            @ApiResponse(responseCode = "404", description = "Short code not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                                    value = """
                                {
                                  "timestamp": "2026-09-12T12:30:00Z",
                                  "status": 404,
                                  "error": "Not Found",
                                  "message": "Short code not found: ABC1234"
                                }
                                """)))
    })
    @GetMapping("/{shortCode:[0-9A-Za-z]{7}}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode){
        String originalUrl=urlService.getOriginalUrl(shortCode);
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(originalUrl)).build();
    }
}
