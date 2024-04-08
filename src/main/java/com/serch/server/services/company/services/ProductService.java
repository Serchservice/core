package com.serch.server.services.company.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.company.responses.ProductResponse;

import java.util.List;

public interface ProductService {
    ApiResponse<List<ProductResponse>> getProducts();
}
