package fr.umlv.LocalKube.app;


import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

import java.util.Collections;
import java.util.Objects;

/**
 * class for sending post request
 */
@Component
class HttpRequest {


    private final RestTemplate restTemplate =  new RestTemplate();
    private final HttpHeaders headers = new HttpHeaders();

    /**
     * post construct methode for the class HttpRequest
     */
    @PostConstruct
    private void postConstruct(){
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    }

    /**
     * send an empty post request to url
     * @param url url to send request to
     * @param <T> response of the request
     * @throws IllegalAccessException in case the request failed
     */
    <T> void post(String url) throws IllegalAccessException {
        Objects.requireNonNull(url);
        HttpEntity<T> entity = new HttpEntity<>(headers);
        try{
            restTemplate.postForObject(url, entity, HttpStatus.class);
        }catch (RestClientException e){
            throw new IllegalAccessException(e.getMessage());
        }

    }


}