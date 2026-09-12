package com.ahmedsamir.urlshortener.controller;

import com.ahmedsamir.urlshortener.dto.CreateUrlRequest;
import com.ahmedsamir.urlshortener.dto.CreateUrlResponse;
import com.ahmedsamir.urlshortener.dto.ErrorResponse;
import com.ahmedsamir.urlshortener.service.UrlService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/urls")
@Tag(name = "URL Shortener", description = "Operations for creating shortened URLs")
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService){
        this.urlService=urlService;
    }

    @Operation(summary = "Create a shortened URL", description = "Creates a short URL for the provided original URL.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Short URL created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreateUrlResponse.class), examples = @ExampleObject(
                                    value = """
                                {
                                  "shortCode": "ABC1234",
                                  "shortUrl": "http://localhost:8080/ABC1234"
                                }
                                """))),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                                    value = """
                                {
                                  "timestamp": "2026-09-12T12:30:00Z",
                                  "status": 400,
                                  "error": "Bad Request",
                                  "message": "Original URL must not be blank"
                                }
                                """)))
    })
    @PostMapping
    public ResponseEntity<CreateUrlResponse> createShortUrl(@Valid @RequestBody CreateUrlRequest request){
        CreateUrlResponse response=urlService.createShortUrl(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
