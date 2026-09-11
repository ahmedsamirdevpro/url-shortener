package com.ahmedsamir.urlshortener.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name="urls")
public class Url {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "short_code",nullable = false,unique = true,length = 16)
    private String shortCode;

    @Column(name = "original_url",nullable = false)
    private String originalUrl;

    @Column(name = "created_at",nullable = false)
    private Instant createdAt;

    protected Url(){

    }

    public Url(String shortCode,String originalUrl){
        this.shortCode=shortCode;
        this.originalUrl=originalUrl;
        this.createdAt=Instant.now();
    }

    public Long getId(){
        return id;
    }
    public String getShortCode(){
        return shortCode;
    }
    public String getOriginalUrl(){
        return originalUrl;
    }
    public Instant getCreatedAt(){
        return createdAt;
    }

}
