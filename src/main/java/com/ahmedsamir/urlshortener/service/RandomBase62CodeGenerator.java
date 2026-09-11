package com.ahmedsamir.urlshortener.service;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class RandomBase62CodeGenerator implements ShortCodeGenerator{
    private static final String BASE62= "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int CODE_LENGTH=7;
    private final SecureRandom random=new SecureRandom();

    @Override
    public String generate() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for(int i=0;i<CODE_LENGTH;i++){
            int index= random.nextInt(BASE62.length());
            code.append(BASE62.charAt(index));
        }
        return code.toString();
    }
}
