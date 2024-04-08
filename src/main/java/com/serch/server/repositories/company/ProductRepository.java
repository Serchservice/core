package com.serch.server.repositories.company;

import com.serch.server.models.company.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, String> {
}