package com.big_hackathon.backend_v2.model;

import lombok.*;

@Getter
@Setter
@ToString(exclude = "password")
@RequiredArgsConstructor
@Builder
public class User {

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private String createdAt;

}
