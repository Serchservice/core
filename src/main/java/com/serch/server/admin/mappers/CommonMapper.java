package com.serch.server.admin.mappers;

import com.serch.server.admin.services.responses.CommonProfileResponse;
import com.serch.server.models.account.Profile;
import com.serch.server.models.auth.User;
import com.serch.server.models.trip.TripShare;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CommonMapper {
    CommonMapper instance = Mappers.getMapper(CommonMapper.class);

    @Mappings({
            @Mapping(target = "category", source = "category.type"),
            @Mapping(target = "image", source = "category.image"),
            @Mapping(target = "name", source = "fullName"),
            @Mapping(target = "emailAddress", source = "user.emailAddress"),
            @Mapping(target = "status", source = "user.status")
    })
    CommonProfileResponse response(Profile profile);

    @Mapping(target = "name", source = "fullName")
    CommonProfileResponse response(User user);

    @Mappings({
            @Mapping(target = "emailAddress", source = "phoneNumber"),
            @Mapping(target = "status", source = "provider.user.status")
    })
    CommonProfileResponse response(TripShare profile);
}
