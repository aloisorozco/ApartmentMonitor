package com.big_hackathon.backend_v2.controller.refreshers;


import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;

@Component
public class GoogleTokenRefresher implements IJwtRefresher{

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;
    
    @Value("${spring.security.oauth2.client.provider.google.token-uri}")
    private String tokenUri;

    @Override
    @SneakyThrows
    public ResponseEntity<String> refreshJWT(String refreshToken) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("refresh_token", refreshToken);
        form.add("grant_type", "refresh_token");
    
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(form, headers);
        RestTemplate restTemplate =  new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(tokenUri, request, String.class);

        if(response.getStatusCode() == HttpStatus.OK){
            Map<String, Object> responseData = new ObjectMapper().readValue(response.getBody(), new TypeReference<Map<String, Object>>() {});
            headers.add("Authorization", "Bearer " + responseData.get("id_token"));
            return ResponseEntity.status(HttpStatus.OK).headers(headers).build();

        }else{
            return new ResponseEntity<>("Failed To Refresh Token", response.getStatusCode());
        }
    }
    
}
