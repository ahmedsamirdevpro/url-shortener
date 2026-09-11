package com.ahmedsamir.urlshortener.service;

import com.ahmedsamir.urlshortener.config.AppProperties;
import com.ahmedsamir.urlshortener.dto.CreateUrlRequest;
import com.ahmedsamir.urlshortener.dto.CreateUrlResponse;
import com.ahmedsamir.urlshortener.entity.Url;
import com.ahmedsamir.urlshortener.exception.CacheOperationException;
import com.ahmedsamir.urlshortener.exception.ShortCodeNotFoundException;
import com.ahmedsamir.urlshortener.repository.UrlRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class UrlServiceImpl implements UrlService {
    private final UrlRepository urlRepository;
    private final ShortCodeGenerator shortCodeGenerator;
    private final UrlPersistenceService urlPersistenceService;
    private final AppProperties appProperties;
    private final UrlCacheService urlCacheService;

    public UrlServiceImpl(UrlRepository urlRepository, ShortCodeGenerator shortCodeGenerator, UrlPersistenceService urlPersistenceService, AppProperties appProperties, UrlCacheService urlCacheService){
        this.urlRepository=urlRepository;
        this.shortCodeGenerator=shortCodeGenerator;
        this.urlPersistenceService = urlPersistenceService;
        this.appProperties = appProperties;
        this.urlCacheService = urlCacheService;
    }

    @Override
    public CreateUrlResponse createShortUrl(CreateUrlRequest request){
        for(int attempt=0;attempt<3;attempt++){
            String shortCode = shortCodeGenerator.generate();
            Url url = new Url(shortCode,request.originalUrl());

            try{
                Url savedUrl = urlPersistenceService.save(url);
                String shortUrl =  appProperties.baseUrl() + "/" + savedUrl.getShortCode();
                return new CreateUrlResponse(savedUrl.getShortCode(),shortUrl);
            }catch (DataIntegrityViolationException exception) {
                if (attempt == 2) {
                    throw exception;
                }

            }
        }
        throw new IllegalStateException("Failed to generate a unique short code");
    }


    @Override
    @Transactional(readOnly=true)
    public String getOriginalUrl(String shortCode){
        String cachedUrl =null;
        try{
            cachedUrl=urlCacheService.get(shortCode);
        }catch (CacheOperationException exception){
            //Redis unavailable
        }
        if(cachedUrl!=null){
            return cachedUrl;
        }
        Url url=urlRepository.findByShortCode(shortCode).orElseThrow(()->new ShortCodeNotFoundException(shortCode));
        try{
            urlCacheService.put(shortCode,url.getOriginalUrl());
        }catch (CacheOperationException exception){
            //Redis unavailable
        }
        return url.getOriginalUrl();
    }

}
