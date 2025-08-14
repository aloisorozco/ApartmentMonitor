package com.big_hackathon.backend_v2.model;

import com.big_hackathon.backend_v2.DTO.RefreshTokenDTO;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "refreshTokens")
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class RefreshToken {

    @Id
    private String userEmail;
    
    @OneToOne
    @MapsId
    @JoinColumn(name = "email")
    private User user;

    @Column(name = "refreshToken")
    private String tokenValue;

    @Column(name = "isActive")
    private boolean isActive;

    @Column(name = "expiresAt")
    private final LocalDateTime expiresAt = LocalDateTime.now().plusDays(3);

    public RefreshTokenDTO refreshTokenDTO(){
        return new RefreshTokenDTO(this);
    }
}
