package com.big_hackathon.backend_v2.DTO;

import com.big_hackathon.backend_v2.model.Apartment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ApartmentDTO {

    private Long apartment_id;
    private double apartment_price;
    private String apartment_location;
    private String apartment_description;
    private String apartment_image_link;
    private String apartment_url;

    public ApartmentDTO(Apartment apartment) {
        this.apartment_id = apartment.getListingID();
        this.apartment_price = apartment.getPrice();
        this.apartment_location = apartment.getLocation();
        this.apartment_description = apartment.getDescription();
        this.apartment_image_link = apartment.getImageLink();
        this.apartment_url = apartment.getUrl();
    }
}
