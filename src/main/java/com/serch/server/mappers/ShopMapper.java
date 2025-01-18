package com.serch.server.mappers;

import com.serch.server.models.shop.Shop;
import com.serch.server.models.shop.ShopDrive;
import com.serch.server.models.shop.ShopSpecialty;
import com.serch.server.models.shop.ShopWeekday;
import com.serch.server.domains.rating.requests.RatingCalculation;
import com.serch.server.domains.shop.requests.CreateShopRequest;
import com.serch.server.domains.shop.requests.ShopDriveRequest;
import com.serch.server.domains.shop.responses.ShopResponse;
import com.serch.server.domains.shop.responses.ShopServiceResponse;
import com.serch.server.domains.shop.responses.ShopWeekdayResponse;
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
            @Mapping(target = "category", source = "category.type"),
            @Mapping(target = "image", source = "category.image"),
            @Mapping(target = "phone", source = "phoneNumber"),
    })
    ShopResponse shop(Shop shop);

    ShopServiceResponse service(ShopSpecialty specialty);

    @Mappings({
            @Mapping(target = "opening", source = "opening", ignore = true),
            @Mapping(target = "closing", source = "closing", ignore = true),
            @Mapping(target = "day", source = "day", ignore = true),
    })
    ShopWeekdayResponse weekday(ShopWeekday weekday);

    ShopDrive drive(ShopDriveRequest request);

    RatingCalculation calculation(ShopDrive drive);
}
