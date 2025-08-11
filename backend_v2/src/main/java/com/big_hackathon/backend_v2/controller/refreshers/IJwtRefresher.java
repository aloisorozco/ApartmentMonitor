package com.big_hackathon.backend_v2.controller.refreshers;

import org.springframework.http.ResponseEntity;

public interface IJwtRefresher {
    public ResponseEntity<String> refreshJWT(String refreshToken);
}