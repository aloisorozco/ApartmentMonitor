package com.big_hackathon.backend_v2.model;

import lombok.*;

@Data
@Builder
public class Apartment {

    private String listingID;

    private String location;

    private String description;

    private String imageLink;

    private double price;

    private String url;

    private double priceTarget;
}
