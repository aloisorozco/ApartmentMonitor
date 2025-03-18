package com.big_hackathon.backend_v2.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Builder
@Entity
public class Apartment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private Long id;

    private String price;

    private double priceTarget;

    private String location;

    private String description;

    private String imageLink;

}
