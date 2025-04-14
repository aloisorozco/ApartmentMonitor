package com.big_hackathon.backend_v2.model;

import lombok.*;

@Getter
@Setter
@ToString(exclude = "password_hashed")
@RequiredArgsConstructor
@Builder
public class User {

    @NonNull
    private String firstName;

    @NonNull
    private String lastName;

    @NonNull
    private String email;

    @NonNull
    private String password_hashed;
    
    @NonNull
    private long createdAt;
}
