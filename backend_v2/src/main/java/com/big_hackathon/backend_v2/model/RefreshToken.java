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
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq_generator")
    @SequenceGenerator(name = "user_seq_generator", allocationSize = 5, initialValue = 1000)
    private Long userID;

    @OneToOne
    @MapsId // tells JPA: "use the same ID as the user"
    @JoinColumn(name = "userID")
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
