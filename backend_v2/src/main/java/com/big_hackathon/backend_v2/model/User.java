package com.big_hackathon.backend_v2.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString(exclude = "password")
@RequiredArgsConstructor
@Builder
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private String createdAt;


    @OneToMany(fetch = FetchType.LAZY)
    private List<Apartment> apartments;

}
