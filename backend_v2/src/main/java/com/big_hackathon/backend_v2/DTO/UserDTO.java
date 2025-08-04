package com.big_hackathon.backend_v2.DTO;

import com.big_hackathon.backend_v2.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserDTO {

    private String user_email;
    private String user_first_name;
    private String user_last_name;
    private List<ApartmentDTO> user_apartment_list;
    private Long userID;
    private User user;

    public UserDTO(User user){
        this.user_email = user.getEmail();
        this.user_first_name = user.getFirstName();
        this.user_last_name = user.getLastName();
        this.userID = user.getUserID();
        (user.getApartments()).forEach(apartment -> {
            user_apartment_list.add(apartment.useDTO());
        });

        this.user = user;
    }
}
