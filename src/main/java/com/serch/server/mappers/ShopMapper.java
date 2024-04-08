package com.serch.server.mappers;

import com.serch.server.models.shop.Shop;
import com.serch.server.models.shop.ShopService;
import com.serch.server.services.shop.requests.CreateShopRequest;
import com.serch.server.services.shop.responses.SearchShopResponse;
import com.serch.server.services.shop.responses.ShopResponse;
import com.serch.server.services.shop.responses.ShopServiceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ShopMapper {
    ShopMapper INSTANCE = Mappers.getMapper(ShopMapper.class);

    @Mapping(target = "services", source = "services", ignore = true)
    Shop shop(CreateShopRequest request);
    ShopResponse shop(Shop shop);
    ShopServiceResponse service(ShopService service);
    SearchShopResponse search(Shop shop);
}
