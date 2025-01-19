package com.serch.server.mappers;

import com.serch.server.core.location.responses.Address;
import com.serch.server.core.location.responses.MapAddress;
import com.serch.server.core.location.responses.PlacesResponse;
import com.serch.server.domains.shop.responses.SearchShopResponse;
import com.serch.server.domains.shop.responses.ShopResponse;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CoreMapper {
    CoreMapper instance = Mappers.getMapper(CoreMapper.class);

    @Mappings({
            @Mapping(
                    target = "phone",
                    expression = """
                        java(place.getNationalPhoneNumber() != null
                            ? place.getNationalPhoneNumber()
                            : place.getInternationalPhoneNumber()
                        )
                    """
            ),
            @Mapping(target = "name", source = "place.displayName.text"),
            @Mapping(target = "link", source = "place.googleMapsLinks.placeUri"),
            @Mapping(target = "image", source = "place.iconMaskBaseUri"),
            @Mapping(target = "open", source = "place.currentOpeningHours.openNow"),
            @Mapping(target = "latitude", source = "place.location.latitude"),
            @Mapping(target = "longitude", source = "place.location.longitude"),
            @Mapping(
                    target = "address",
                    expression = """
                            java(place.getShortFormattedAddress() != null && place.getDisplayName() != null
                                ? String.format("%s, %s", place.getDisplayName().getText(), place.getShortFormattedAddress())
                                : place.getDisplayName() != null
                                ? place.getDisplayName().getText()
                                : place.getFormattedAddress()
                            )
                    """
            ),
            @Mapping(
                    target = "status",
                    expression = """
                        java(place.getBusinessStatus().equalsIgnoreCase("operational")
                            ? com.serch.server.enums.shop.ShopStatus.OPEN
                            : com.serch.server.enums.shop.ShopStatus.SUSPENDED
                        )
                    """
            ),
            @Mapping(target = "category", expression = "java(type.replaceAll(\"_\", \" \"))")
    })
    ShopResponse response(PlacesResponse.Place place, String type);

    @Mappings({
            @Mapping(
                    target = "place",
                    expression = """
                        java(mapAddress.getShortFormattedAddress() != null && mapAddress.getDisplayName() != null
                            ? String.format("%s, %s", mapAddress.getDisplayName().getText(), mapAddress.getShortFormattedAddress())
                            : mapAddress.getDisplayName() != null
                            ? mapAddress.getDisplayName().getText()
                            : mapAddress.getFormattedAddress()
                        )
                    """
            ),
            @Mapping(
                    target = "country",
                    expression = "java(CoreMapper.getAddressComponentValue(mapAddress.getAddressComponents(), \"country\"))"
            ),
            @Mapping(
                    target = "state",
                    expression = "java(CoreMapper.getAddressComponentValue(mapAddress.getAddressComponents(), \"administrative_area_level_1\"))"
            ),
            @Mapping(
                    target = "city",
                    expression = "java(CoreMapper.getAddressComponentValue(mapAddress.getAddressComponents(), \"locality\"))"
            ),
            @Mapping(
                    target = "localGovernmentArea",
                    expression = "java(CoreMapper.getAddressComponentValue(mapAddress.getAddressComponents(), \"administrative_area_level_2\"))"
            ),
            @Mapping(
                    target = "streetNumber",
                    expression = "java(CoreMapper.getAddressComponentValue(mapAddress.getAddressComponents(), \"plus_code\"))"
            ),
            @Mapping(
                    target = "streetName",
                    expression = "java(CoreMapper.getAddressComponentValue(mapAddress.getAddressComponents(), \"administrative_area_level_3\"))"
            ),
            @Mapping(target = "latitude", source = "location.latitude"),
            @Mapping(target = "longitude", source = "location.longitude"),
    })
    Address address(MapAddress mapAddress);

    static String getAddressComponentValue(List<MapAddress.AddressComponent> components, String type) {
        for (MapAddress.AddressComponent component : components) {
            if (component.getTypes().contains(type)) {
                return component.getLongText();
            }
        }
        return null;
    }

    @Mappings({
            @Mapping(
                    target = "distanceInKm",
                    expression = """
                        java(String.format("%s km", com.serch.server.utils.HelperUtil.getDistance(latitude, longitude, place.getLocation().getLatitude(), place.getLocation().getLongitude())))
                    """
            ),
            @Mapping(
                    target = "distance",
                    expression = """
                        java(com.serch.server.utils.HelperUtil.getDistance(latitude, longitude, place.getLocation().getLatitude(), place.getLocation().getLongitude()))
                    """
            ),
            @Mapping(target = "shop", expression = "java(response(place, type))"),
            @Mapping(target = "isGoogle", constant = "true"),
    })
    SearchShopResponse response(PlacesResponse.Place place, double latitude, double longitude, String type);
}