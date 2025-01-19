package com.serch.server.admin.mappers;

import com.serch.server.admin.models.Admin;
import com.serch.server.admin.services.responses.CommonProfileResponse;
import com.serch.server.models.account.BusinessProfile;
import com.serch.server.models.account.Profile;
import com.serch.server.models.auth.User;
import com.serch.server.models.shared.Guest;
import com.serch.server.models.trip.TripShare;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommonMapper {
    CommonMapper instance = Mappers.getMapper(CommonMapper.class);

    @Mappings({
            @Mapping(target = "category", source = "category.type"),
            @Mapping(target = "image", source = "category.image"),
            @Mapping(target = "name", source = "fullName"),
            @Mapping(target = "emailAddress", source = "user.emailAddress"),
            @Mapping(target = "status", source = "user.status"),
            @Mapping(target = "role", source = "user.role"),
            @Mapping(target = "rating", source = "rating"),
            @Mapping(target = "firstName", source = "user.firstName"),
            @Mapping(target = "lastName", source = "user.lastName"),
    })
    CommonProfileResponse response(Profile profile);

    @Mappings({
            @Mapping(target = "name", source = "fullName"),
            @Mapping(target = "emailAddress", source = "user.emailAddress"),
            @Mapping(target = "firstName", source = "user.firstName"),
            @Mapping(target = "lastName", source = "user.lastName"),
            @Mapping(target = "status", source = "user.status"),
            @Mapping(target = "role", source = "user.role"),
    })
    CommonProfileResponse response(Admin profile);

    @Mappings({
            @Mapping(target = "category", source = "category.type"),
            @Mapping(target = "image", source = "category.image"),
            @Mapping(target = "name", source = "businessName"),
            @Mapping(target = "emailAddress", source = "user.emailAddress"),
            @Mapping(target = "status", source = "user.status"),
            @Mapping(target = "avatar", source = "businessLogo"),
            @Mapping(target = "role", source = "user.role"),
            @Mapping(target = "rating", source = "rating"),
            @Mapping(target = "firstName", source = "user.firstName"),
            @Mapping(target = "lastName", source = "user.lastName"),
    })
    CommonProfileResponse response(BusinessProfile profile);

    @Mapping(target = "name", source = "fullName")
    CommonProfileResponse response(Guest profile);

    @Mapping(target = "name", source = "fullName")
    CommonProfileResponse response(User user);

    @Mappings({
            @Mapping(target = "emailAddress", source = "phoneNumber"),
            @Mapping(target = "status", source = "provider.user.status"),
            @Mapping(target = "rating", source = "provider.rating"),
    })
    CommonProfileResponse response(TripShare profile);
}
