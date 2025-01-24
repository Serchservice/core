package com.serch.server.mappers;

import com.serch.server.domains.auth.requests.RequestProfile;
import com.serch.server.domains.shared.requests.CreateGuestRequest;
import com.serch.server.domains.shared.requests.GuestToUserRequest;
import com.serch.server.domains.shared.responses.*;
import com.serch.server.models.account.Profile;
import com.serch.server.models.shared.Guest;
import com.serch.server.models.shared.SharedLink;
import com.serch.server.models.shared.SharedStatus;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface SharedMapper {
    SharedMapper INSTANCE = Mappers.getMapper(SharedMapper.class);

    SharedLinkData response(SharedLink link);

    @Mapping(target = "id", source = "id")
    SharedProfileData response(Profile profile);

    @Mappings({
            @Mapping(target = "device", source = "device.device"),
            @Mapping(target = "name", source = "device.name"),
            @Mapping(target = "platform", source = "device.platform"),
            @Mapping(target = "host", source = "device.host"),
            @Mapping(target = "ipAddress", source = "device.ipAddress"),
            @Mapping(target = "localHost", source = "device.localHost"),
            @Mapping(target = "operatingSystem", source = "device.operatingSystem"),
            @Mapping(target = "operatingSystemVersion", source = "device.operatingSystemVersion"),
    })
    Guest guest(CreateGuestRequest request);

    Guest guest(Profile profile);

    @Mappings({
            @Mapping(target = "firstName", source = "user.firstName"),
            @Mapping(target = "lastName", source = "user.lastName"),
            @Mapping(target = "emailAddress", source = "user.emailAddress"),
    })
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
            @Mapping(target = "status", source = "useStatus", ignore = true),
            @Mapping(target = "trip", source = "trip", ignore = true),
    })
    SharedStatusData data(SharedStatus status);
}
