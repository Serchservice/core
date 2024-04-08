package com.serch.server.mappers;

import com.serch.server.models.company.Product;
import com.serch.server.services.company.responses.ProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CompanyMapper {
    CompanyMapper INSTANCE = Mappers.getMapper(CompanyMapper.class);

    ProductResponse product(Product product);
}
