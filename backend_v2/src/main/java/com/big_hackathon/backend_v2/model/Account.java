package com.big_hackathon.backend_v2.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@Builder
@Entity
public class Account {

    @Id
    @JsonProperty("email")
    private String email;

    @JsonProperty("password")
    private String password;

    @OneToMany
    private List<Apartment> apartments;

    @Override
    public String toString() {
        return "email: " + email + " password: " + password;
    }

}
