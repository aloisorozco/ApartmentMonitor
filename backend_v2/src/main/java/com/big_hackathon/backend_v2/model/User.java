package com.big_hackathon.backend_v2.model;

import com.big_hackathon.backend_v2.DTO.UserDTO;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class User {

    @Id
    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "authority")
    private String authority;

    @Column(name = "password_hash")
    private String hashedPassword;

    @Column(name = "created_at")
    private LocalDateTime addedAt;

    @PrePersist
    public void generateUser(){
        this.addedAt = LocalDateTime.now();
    }

    //M2M unidirectional
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "user_watchlist",
            joinColumns = @JoinColumn(name = "email", referencedColumnName = "email"),
            inverseJoinColumns = @JoinColumn(name = "listing_id", referencedColumnName = "listing_id"))
    private List<Apartment> apartments = new ArrayList<>();

    
    public UserDTO useDTO(){
        return new UserDTO(this);
    }
}
