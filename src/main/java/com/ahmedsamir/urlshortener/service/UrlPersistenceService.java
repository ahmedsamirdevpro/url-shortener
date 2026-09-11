package com.ahmedsamir.urlshortener.service;

import com.ahmedsamir.urlshortener.entity.Url;
import com.ahmedsamir.urlshortener.repository.UrlRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UrlPersistenceService {

    private final UrlRepository urlRepository;

    public UrlPersistenceService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Url save(Url url) {
        return urlRepository.saveAndFlush(url);
    }
}