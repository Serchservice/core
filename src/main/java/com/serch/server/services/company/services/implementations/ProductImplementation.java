package com.serch.server.services.company.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.mappers.CompanyMapper;
import com.serch.server.repositories.company.ProductRepository;
import com.serch.server.services.company.responses.ProductResponse;
import com.serch.server.services.company.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductImplementation implements ProductService {
    private final ProductRepository productRepository;

    @Override
    public ApiResponse<List<ProductResponse>> getProducts() {
        List<ProductResponse> list = productRepository.findAll()
                .stream()
                .map(CompanyMapper.INSTANCE::product)
                .toList();
        return new ApiResponse<>(list);
    }
}
