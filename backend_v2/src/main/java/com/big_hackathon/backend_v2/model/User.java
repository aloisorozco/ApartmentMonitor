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
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq_generator")
    @SequenceGenerator(name = "user_seq_generator", allocationSize = 5, initialValue = 1000)
    private Long userID;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "password_hash")
    private String hashedPassword;

    @Column(name = "created_at")
    private LocalDateTime addedAt = LocalDateTime.now();

    //M2M unidirectional
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "user_apartment_watchlist",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "listing_id", referencedColumnName = "listing_id"))
    private List<Apartment> apartments = new ArrayList<>();

    public UserDTO useDTO(){
        return new UserDTO(this);
    }
}
