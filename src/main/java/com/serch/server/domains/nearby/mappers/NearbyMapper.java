package com.serch.server.domains.nearby.mappers;

import com.serch.server.domains.nearby.models.Nearby;
import com.serch.server.domains.nearby.models.NearbyShop;
import com.serch.server.domains.nearby.services.auth.requests.NearbyDeviceRequest;
import com.serch.server.domains.nearby.services.drive.requests.NearbyDriveRequest;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface NearbyMapper {
    NearbyMapper instance = Mappers.getMapper(NearbyMapper.class);

    @Mapping(target = "id", ignore = true)
    void updateNearbyFromDto(NearbyDeviceRequest source, @MappingTarget Nearby target);

    @Mapping(target = "id", source = "id", ignore = true)
    Nearby nearby(NearbyDeviceRequest source);

    NearbyShop shop(NearbyDriveRequest request);
}