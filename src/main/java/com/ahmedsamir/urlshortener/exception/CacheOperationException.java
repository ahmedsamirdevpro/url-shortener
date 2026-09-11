package com.ahmedsamir.urlshortener.exception;

public class CacheOperationException extends RuntimeException {
    public CacheOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}