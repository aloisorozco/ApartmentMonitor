package com.big_hackathon.backend_v2.DTO;

import com.big_hackathon.backend_v2.model.User;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {

    @JsonProperty("user_email")
    private String user_email;

    @JsonProperty("user_first_name")    
    private String user_first_name;

    @JsonProperty("user_last_name")
    private String user_last_name;

    @JsonProperty("user_apartment_list")
    private List<ApartmentDTO> user_apartment_list;

    public UserDTO(User user){
        this.user_email = user.getEmail();
        this.user_first_name = user.getFirstName();
        this.user_last_name = user.getLastName();
        
    }
}
