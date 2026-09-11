package com.ahmedsamir.urlshortener.repository;

import com.ahmedsamir.urlshortener.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url,Long> {
    Optional<Url>findByShortCode(String shortCode);
}
