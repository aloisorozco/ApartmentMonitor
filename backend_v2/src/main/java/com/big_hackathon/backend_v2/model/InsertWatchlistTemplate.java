package com.big_hackathon.backend_v2.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class InsertWatchlistTemplate {

    private String email;

    private String listingId;

    private double targetPrice;
}
