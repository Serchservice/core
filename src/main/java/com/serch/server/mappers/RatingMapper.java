package com.serch.server.mappers;

import com.serch.server.models.rating.AppRating;
import com.serch.server.models.rating.Rating;
import com.serch.server.services.rating.requests.RateAppRequest;
import com.serch.server.services.rating.requests.RatingCalculation;
import com.serch.server.services.rating.responses.RatingResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RatingMapper {
    RatingMapper INSTANCE = Mappers.getMapper(RatingMapper.class);

    AppRating rating(RateAppRequest request);

    RatingResponse response(Rating rating);

    RatingCalculation calculation(Rating rating);
}
