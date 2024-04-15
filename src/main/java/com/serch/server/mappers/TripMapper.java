package com.serch.server.mappers;

import com.serch.server.models.account.Profile;
import com.serch.server.models.trip.Active;
import com.serch.server.services.trip.requests.OnlineRequest;
import com.serch.server.services.trip.responses.ActiveResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TripMapper {
    TripMapper INSTANCE = Mappers.getMapper(TripMapper.class);

    Active active(OnlineRequest request);
    ActiveResponse response(Profile profile);
}
