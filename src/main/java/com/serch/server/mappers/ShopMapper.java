package com.serch.server.mappers;

import com.serch.server.models.shop.Shop;
import com.serch.server.services.shop.requests.CreateShopRequest;
import com.serch.server.services.shop.responses.ShopResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ShopMapper {
    ShopMapper INSTANCE = Mappers.getMapper(ShopMapper.class);

    @Mappings({
            @Mapping(target = "services", source = "services", ignore = true),
            @Mapping(target = "weekdays", source = "weekdays", ignore = true),
    })
    Shop shop(CreateShopRequest request);
    @Mappings({
            @Mapping(target = "services", source = "services", ignore = true),
            @Mapping(target = "weekdays", source = "weekdays", ignore = true),
            @Mapping(target = "category", source = "category", ignore = true),
            @Mapping(target = "phone", source = "phoneNumber"),
    })
    ShopResponse shop(Shop shop);
}
