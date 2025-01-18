package com.serch.server.domains.rating.requests;

import lombok.Data;


/**
 * {@link RateAppRequest} is for in-app rating in any of the Serch cross-platform technology.
 * <p></p>
 * @field account - A generic name for the User, Provider, Business or Guest Id that is rating the app.
 * @field rating - The value of the rating in double 5.0-0.0
 * @field comment - The optional comment for the rating
 * <p></p>
 * The data from this class is sent and stored in the {@link com.serch.server.models.rating.AppRating} table
 *
 * @see com.serch.server.domains.rating.services.RatingImplementation
 * @see com.serch.server.domains.rating.services.RatingService
 */
@Data
public class RateAppRequest {
    private Double rating;
    private String account;
    private String comment;
}