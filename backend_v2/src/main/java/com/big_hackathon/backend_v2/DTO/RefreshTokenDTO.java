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

    private String userEmail;
    private String tokenValue;
    private final LocalDateTime expiresAt;
    private boolean isActive;

    public RefreshTokenDTO(RefreshToken token){
        this.userEmail = token.getUser().getEmail();
        this.tokenValue = token.getTokenValue();
        this.expiresAt = token.getExpiresAt();
        this.isActive = token.isActive();
    }
}