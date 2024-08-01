package com.serch.server.mappers;

import com.serch.server.core.map.responses.Address;
import com.serch.server.core.map.responses.LocationIQPlace;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CoreMapper {
    CoreMapper INSTANCE = Mappers.getMapper(CoreMapper.class);

    @Mappings({
            @Mapping(target = "id", source = "placeId"),
            @Mapping(target = "place", source = "displayName"),
            @Mapping(target = "latitude", source = "lat"),
            @Mapping(target = "longitude", source = "lon"),
            @Mapping(target = "localGovernmentArea", source = "address.road"),
            @Mapping(target = "streetNumber", source = "address.houseNumber"),
            @Mapping(target = "country", source = "address.country"),
            @Mapping(target = "state", source = "address.state"),
            @Mapping(target = "city", source = "address.city"),
            @Mapping(target = "streetName", source = "address.name"),
    })
    Address address(LocationIQPlace place);
}