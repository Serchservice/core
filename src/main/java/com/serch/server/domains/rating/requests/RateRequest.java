package com.serch.server.domains.rating.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * {@link RateRequest} is used to rate any Serch product including but not limited to:
 * <p> </p>
 * Schedule, Trip, Call (Voice Call and Tip2Fix).
 * <p></p>
 * If it is Tip2Fix, then there is a need to watch out for isProvider. If it is true, then the user/guest
 * is rating the provider that attended the tip2Fix session/s. Else, the user/guest is rating the tip2Fix call.
 * <p></p>
 * The data from this class is sent and stored in the {@link com.serch.server.models.rating.Rating} table
 *
 * @see com.serch.server.domains.rating.services.RatingImplementation
 * @see com.serch.server.domains.rating.services.RatingService
 */
@Data
public class RateRequest {
    private String id;
    private Double rating;
    private Double invited;
    private String guest;
    private String comment;

    @JsonProperty("is_both")
    private Boolean isBoth = false;
}