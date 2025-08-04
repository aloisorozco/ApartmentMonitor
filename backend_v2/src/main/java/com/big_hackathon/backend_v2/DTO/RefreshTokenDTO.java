package com.big_hackathon.backend_v2.DTO;

import java.time.LocalDateTime;
import com.big_hackathon.backend_v2.model.RefreshToken;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@Builder
public class RefreshTokenDTO {

    private long user_id;
    private String tokenValue;
    private final LocalDateTime expiresAt;
    private boolean isActive;

    public RefreshTokenDTO(RefreshToken token){
        this.user_id = token.getUserID();
        this.tokenValue = token.getTokenValue();
        this.expiresAt = token.getExpiresAt();
        this.isActive = token.isActive();
    }
}