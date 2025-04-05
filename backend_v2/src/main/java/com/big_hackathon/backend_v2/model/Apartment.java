package com.big_hackathon.backend_v2.model;

import lombok.*;

@Data
@Builder
public class Apartment {

    private String price;

    private double priceTarget;

    private String location;

    private String description;

    private String imageLink;
}
