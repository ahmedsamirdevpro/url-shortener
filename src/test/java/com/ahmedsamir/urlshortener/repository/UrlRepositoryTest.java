package com.ahmedsamir.urlshortener.repository;

import com.ahmedsamir.urlshortener.entity.Url;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@SpringBootTest
@Transactional
public class UrlRepositoryTest {
    @Autowired
    private UrlRepository urlRepository;

    @Test
    void shodSaveUrl(){
        Url url=new Url("abd","www.ahmed.com");

        Url savedUrl=urlRepository.save(url);

        assertThat(savedUrl.getId()).isNotNull();
    }

    @Test
    void shouldFindUrlByShortCode(){
        Url url=new Url("abcdef","www.ahmedsamir.com");
        urlRepository.save(url);

        Optional<Url> foundUrl=urlRepository.findByShortCode("abcdef");

        assertThat(foundUrl).isPresent();

        assertThat(foundUrl.get().getOriginalUrl()).isEqualTo("www.ahmedsamir.com");
    }

    @Test
    void shouldNotAllowDuplicateShortCode() {

        Url firstUrl = new Url("duplicate", "https://first.com");
        Url secondUrl = new Url("duplicate", "https://second.com");
        urlRepository.save(firstUrl);
        assertThatThrownBy(() -> urlRepository.saveAndFlush(secondUrl)).isInstanceOf(DataIntegrityViolationException.class);
    }
}
