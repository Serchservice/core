package com.serch.server.nearby.mappers;

import com.serch.server.nearby.models.Nearby;
import com.serch.server.nearby.models.NearbyShop;
import com.serch.server.nearby.services.auth.requests.NearbyDeviceRequest;
import com.serch.server.nearby.services.drive.requests.NearbyDriveRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface NearbyMapper {
    NearbyMapper instance = Mappers.getMapper(NearbyMapper.class);

    @Mapping(target = "id", ignore = true)
    void updateNearbyFromDto(NearbyDeviceRequest source, @MappingTarget Nearby target);

    Nearby nearby(NearbyDeviceRequest source);

    NearbyShop shop(NearbyDriveRequest request);
}