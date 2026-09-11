package com.ahmedsamir.urlshortener.exception;

public class ShortCodeNotFoundException extends RuntimeException{
    public ShortCodeNotFoundException(String shortCode){
        super("Short Code Not Found: "+shortCode);
    }
}
