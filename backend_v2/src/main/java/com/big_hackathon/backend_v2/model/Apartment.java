package com.big_hackathon.backend_v2.model;

import com.big_hackathon.backend_v2.DTO.ApartmentDTO;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "apartments")
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Apartment {

    //TODO: unique ID should be GeoCache ID
    @Id //Primary Key
    @Column(name = "listing_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "apart_seq_generator")
    @SequenceGenerator(name = "apart_seq_generator", initialValue = 1, allocationSize = 5)
    private Long listingID;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "image_link")
    private String imageLink;

    @Column(name = "url", nullable = false, unique = true)
    private String url;

    // //args constructor without the P.K since seq generated
    public Apartment(double price, String location, String description, String imageLink, String url){
        this.price = price;
        this.location = location;
        this.description = description;
        this.imageLink = imageLink;
        this.url = url;
    }

    public ApartmentDTO useDTO() {
        return new ApartmentDTO(this);
    }
}
