package com.SE330_Q22.product_service.repository;

import com.SE330_Q22.product_service.dto.ProductResponse;
import com.SE330_Q22.product_service.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findBySkuCode(String skuCode);
    boolean existsBySkuCode(String skuCode);
}
