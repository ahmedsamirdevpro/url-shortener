package com.ahmedsamir.urlshortener.service;

import com.ahmedsamir.urlshortener.dto.CreateUrlRequest;
import com.ahmedsamir.urlshortener.dto.CreateUrlResponse;

public interface UrlService {
    CreateUrlResponse createShortUrl(CreateUrlRequest request);
    String getOriginalUrl (String shortCode);
}
