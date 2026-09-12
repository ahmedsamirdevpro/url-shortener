package com.ahmedsamir.urlshortener.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateUrlResponse (@Schema(description = "The generated 7-character Base62 short code", example = "ABC1234")String shortCode ,
                                 @Schema(description = "The complete shortened URL", example = "http://localhost:8080/ABC1234")String shortUrl){
}
