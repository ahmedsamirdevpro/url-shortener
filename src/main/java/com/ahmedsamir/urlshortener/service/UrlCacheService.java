package com.ahmedsamir.urlshortener.service;

public interface UrlCacheService {
    String get(String shortCode);
    void put(String shortCode, String originalUrl);
}