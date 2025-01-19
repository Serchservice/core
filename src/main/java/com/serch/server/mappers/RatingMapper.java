package com.serch.server.mappers;

import com.serch.server.models.rating.AppRating;
import com.serch.server.models.rating.Rating;
import com.serch.server.domains.rating.requests.RateAppRequest;
import com.serch.server.domains.rating.requests.RatingCalculation;
import com.serch.server.domains.rating.responses.RatingResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface RatingMapper {
    RatingMapper INSTANCE = Mappers.getMapper(RatingMapper.class);

    AppRating rating(RateAppRequest request);

    RatingResponse response(Rating rating);

    RatingCalculation calculation(Rating rating);
}
