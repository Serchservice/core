package com.serch.server.mappers;

import com.serch.server.models.account.Profile;
import com.serch.server.models.shared.Guest;
import com.serch.server.models.shared.SharedLink;
import com.serch.server.models.shared.SharedStatus;
import com.serch.server.services.auth.requests.RequestProfile;
import com.serch.server.services.shared.requests.CreateGuestRequest;
import com.serch.server.services.shared.requests.GuestToUserRequest;
import com.serch.server.services.shared.responses.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SharedMapper {
    SharedMapper INSTANCE = Mappers.getMapper(SharedMapper.class);

    SharedLinkData response(SharedLink link);
    @Mapping(target = "id", source = "id")
    SharedProfileData response(Profile profile);

    @Mapping(target = "device", source = "device", ignore = true)
    Guest guest(CreateGuestRequest request);
    Guest guest(Profile profile);
    CreateGuestRequest request(Profile profile);
    GuestProfileData response(Guest guest);
    RequestProfile profile(GuestToUserRequest request);
    @Mappings({
            @Mapping(target = "linkId", source = "id"),
            @Mapping(target = "amount", source = "amount", ignore = true),
    })
    SharedLinkData shared(SharedLink link);
    @Mapping(target = "gender", source = "gender", ignore = true)
    GuestResponse guest(Guest guest);

    @Mappings({
            @Mapping(target = "user", source = "user", ignore = true),
            @Mapping(target = "provider", source = "provider", ignore = true),
            @Mapping(target = "amount", source = "amount", ignore = true),
            @Mapping(target = "status", source = "status", ignore = true),
            @Mapping(target = "trip", source = "trip", ignore = true),
    })
    SharedStatusData data(SharedStatus status);
}
