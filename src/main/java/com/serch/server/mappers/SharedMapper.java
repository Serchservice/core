package com.serch.server.mappers;

import com.serch.server.models.account.Profile;
import com.serch.server.models.shared.Guest;
import com.serch.server.models.shared.SharedLink;
import com.serch.server.models.shared.SharedPricing;
import com.serch.server.services.auth.requests.RequestProfile;
import com.serch.server.services.shared.requests.CreateGuestRequest;
import com.serch.server.services.shared.requests.GuestToUserRequest;
import com.serch.server.services.shared.responses.GuestProfileData;
import com.serch.server.services.shared.responses.SharedLinkData;
import com.serch.server.services.shared.responses.SharedPricingData;
import com.serch.server.services.shared.responses.SharedProfileData;
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
    @Mapping(target = "avatar", source = "avatar", ignore = true)
    Guest guest(CreateGuestRequest request);
    Guest guest(Profile profile);
    GuestProfileData response(Guest guest);
    RequestProfile profile(GuestToUserRequest request);

    @Mappings({
            @Mapping(target = "total", source = "amount"),
            @Mapping(target = "guest", source = "guest", ignore = true)
    })
    SharedPricingData data(SharedPricing pricing);
}
